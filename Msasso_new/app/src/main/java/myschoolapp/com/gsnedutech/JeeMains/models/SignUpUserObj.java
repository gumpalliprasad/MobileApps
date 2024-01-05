package myschoolapp.com.gsnedutech.JeeMains.models;

public class SignUpUserObj {
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthValue() {
        return authValue;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(String userVerified) {
        this.userVerified = userVerified;
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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public UserProfile getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(UserProfile userProfiles) {
        this.userProfiles = userProfiles;
    }

    String authType;
    String authValue;
    String userToken;
    String userType;
    String userVerified;
    String userName;
    String userEmail;
    String mobileNo;
    UserProfile userProfiles;
}
