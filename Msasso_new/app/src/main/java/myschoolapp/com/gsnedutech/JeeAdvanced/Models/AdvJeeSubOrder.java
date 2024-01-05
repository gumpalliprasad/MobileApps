package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;

public class AdvJeeSubOrder implements Serializable {

    String sId;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    String sName;
}
