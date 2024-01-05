package myschoolapp.com.gsnedutech.Models;

public class SubScoreCard {
    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getCorAns() {
        return corAns;
    }

    public void setCorAns(int corAns) {
        this.corAns = corAns;
    }

    public int getWrgAns() {
        return wrgAns;
    }

    public void setWrgAns(int wrgAns) {
        this.wrgAns = wrgAns;
    }

    public int getUnAns() {
        return unAns;
    }

    public void setUnAns(int unAns) {
        this.unAns = unAns;
    }

    String subName;
    int corAns, wrgAns, unAns,gAns;
    int cMarks;
    int wMarks;
    int uMarks;

    public int getgAns() {
        return gAns;
    }

    public void setgAns(int gAns) {
        this.gAns = gAns;
    }

    public int getcMarks() {
        return cMarks;
    }

    public void setcMarks(int cMarks) {
        this.cMarks = cMarks;
    }

    public int getwMarks() {
        return wMarks;
    }

    public void setwMarks(int wMarks) {
        this.wMarks = wMarks;
    }

    public int getuMarks() {
        return uMarks;
    }

    public void setuMarks(int uMarks) {
        this.uMarks = uMarks;
    }

    public int getgMarks() {
        return gMarks;
    }

    public void setgMarks(int gMarks) {
        this.gMarks = gMarks;
    }

    int gMarks;
}
