package myschoolapp.com.gsnedutech.JeeMains.models;

public class LoginReq {

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    String mobileNo, password;

}
