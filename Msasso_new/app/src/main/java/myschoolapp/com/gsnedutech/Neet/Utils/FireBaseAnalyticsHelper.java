package myschoolapp.com.gsnedutech.Neet.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;


public class FireBaseAnalyticsHelper {

    FirebaseAnalytics mFirebaseAnalytics;

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


}
