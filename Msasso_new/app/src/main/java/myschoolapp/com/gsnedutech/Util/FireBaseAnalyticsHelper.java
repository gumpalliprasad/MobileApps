package myschoolapp.com.gsnedutech.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import myschoolapp.com.gsnedutech.Models.StudentObj;

import static android.content.Context.MODE_PRIVATE;

public class FireBaseAnalyticsHelper {

    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    public FireBaseAnalyticsHelper(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logDeviceIdEvent(String deviceId){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Device_Id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, deviceId);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void logModelEvent(String mobileModel){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "mobileModel");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mobileModel);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void logVersionEvent(String version){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "version_code");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, version);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }



    public void logScreenEvent(Activity activity, String screenName){
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, screenName);
    }

    public void logUserId(Activity activity){
        sh_Pref = activity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        mFirebaseAnalytics.setUserId(sObj.getStudentId());
    }

}
