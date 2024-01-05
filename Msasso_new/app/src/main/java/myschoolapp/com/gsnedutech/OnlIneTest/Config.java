package myschoolapp.com.gsnedutech.OnlIneTest;

import java.util.ArrayList;

public class Config {

    String _id;
    int s3Sync;
    int dbSync;
    String schemaName;
    SThreeDet s3Details;
    ArrayList<String> browsersList;
    ArrayList<String> osList;
    ArrayList<String> androidVersions;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getS3Sync() {
        return s3Sync;
    }

    public void setS3Sync(int s3Sync) {
        this.s3Sync = s3Sync;
    }

    public int getDbSync() {
        return dbSync;
    }

    public void setDbSync(int dbSync) {
        this.dbSync = dbSync;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public SThreeDet getS3Details() {
        return s3Details;
    }

    public void setS3Details(SThreeDet s3Details) {
        this.s3Details = s3Details;
    }

    public ArrayList<String> getBrowsersList() {
        return browsersList;
    }

    public void setBrowsersList(ArrayList<String> browsersList) {
        this.browsersList = browsersList;
    }

    public ArrayList<String> getOsList() {
        return osList;
    }

    public void setOsList(ArrayList<String> osList) {
        this.osList = osList;
    }

    public ArrayList<String> getAndroidVersions() {
        return androidVersions;
    }

    public void setAndroidVersions(ArrayList<String> androidVersions) {
        this.androidVersions = androidVersions;
    }

    public ArrayList<String> getIosVersions() {
        return iosVersions;
    }

    public void setIosVersions(ArrayList<String> iosVersions) {
        this.iosVersions = iosVersions;
    }

    ArrayList<String> iosVersions;

    public class SThreeDet {
        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getaKey() {
            return aKey;
        }

        public void setaKey(String aKey) {
            this.aKey = aKey;
        }

        public String getsKey() {
            return sKey;
        }

        public void setsKey(String sKey) {
            this.sKey = sKey;
        }

        String bucketName, aKey, sKey;
    }
}
