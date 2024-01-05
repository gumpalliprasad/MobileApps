package myschoolapp.com.gsnedutech.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.UserSelection;
import okhttp3.Headers;

public class MyUtils {

    /**
     * Method to check for the availability of the local data
     */

    
    public static int[] backgrounds = {R.drawable.bg_palet_d1,R.drawable.bg_palet_d2,R.drawable.bg_palet_d3,R.drawable.bg_palet_d4};
    
     public Dialog loading = null;

    public static boolean isFileDataAvailableOrNot(Context context, String path) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!new File(context.getExternalFilesDir(null) + path).exists()) {
                return false;
            } else
                return new File(context.getExternalFilesDir(null) + path).exists();
        }else {
            if (!new File(Environment.getExternalStorageDirectory() + path).exists()) {
                return false;
            } else
                return new File(Environment.getExternalStorageDirectory() + path).exists();
        }

    }

    /**
     * Generic Alert Box to be called from anny Activity of the Project
     * we will be calling this function using func_id
     * func_id - 1 = Internet
     * func_id - 2 = Generic
     **/

//    Alert Dialogue Box

    public AlertDialog alert = null;

    public void alertDialog(final int func_id, final Activity activity, String title, String msg, String pbtn, String nbtn,Boolean cancelable) {

        if (alert == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(msg)
                    .setTitle(title)
                    .setCancelable(cancelable)
                    .setPositiveButton(pbtn,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (func_id == 1)
                                        activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                    else if (func_id == 2)
                                        activity.finish();

                                    dialog.dismiss();
                                }
                            }
                    );

//                .setNegativeButton(nbtn,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                if (func_id == 2)
//                                    activity.finish();
//                                dialog.dismiss();
//
//                            }
//                        }
//                );
            alert = builder.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#2F23AF"));
//                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                    //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                }
            });
        }

        alert.show();
    }

    public void dismissAlertDialog(){
        if (alert!=null)
        alert.dismiss();
    }


    public static double roundTwoDecimals(double tqe) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(tqe));
    }



    public String formateSeconds(int sec) {
        double hours, minutes, seconds;
        hours = sec / 3600;
        minutes = (sec % 3600) / 60;
        seconds = sec % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public class Utility {
        public  int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
            return noOfColumns;
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public void showLoader(Context context){

        try {
            if (loading==null){
                loading = new Dialog(context);
                loading.setContentView(R.layout.dialog_loader);
                loading.setCancelable(false);
                loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            loading.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void dismissDialog(){
        try {
            if (loading!=null && loading.isShowing()){
                loading.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showLog(String TAG,String value){
        Log.v(TAG,value);
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public Date getDateFromString(String dateFormat, String dateString){
        Date dt = new Date();
        try {
            dt = new SimpleDateFormat(dateFormat).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public String getStringFromDate(String dateFormat, Date date){
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public String cleanWebString(String webString){
        webString = webString.replaceAll("<span.*?>", "");
        webString = webString.replaceAll("&#39;", "");
        webString = webString.replaceAll("(?s)<!--.*?-->","");
        webString = webString.replaceAll("</span.*?>","");
        return webString;
    }

    /**
     * Common header for all apis
     * key's
     * Authorization = token
     * userId :- studenId/userId
     * roleId = for student we need to pass 5 for others we need to pass updated values
     * code = schema data
     */
    public static Headers addHeaders(SharedPreferences shPref) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConst.AUTHORIZATION, "Bearer "+shPref.getString(AppConst.ACCESS_TOKEN, ""));
        headers.put(AppConst.USER_ID, shPref.getString(AppConst.USER_ID_DATA, ""));
        String roleId = "5";
        if(!shPref.getString(AppConst.USER_SELECTED, "").equalsIgnoreCase(AppConst.USER_STUDENT)){
            roleId = shPref.getString(AppConst.USER_ROLE_ID, "");
        }
        headers.put(AppConst.ROLE_ID, roleId);
        headers.put(AppConst.CODE, shPref.getString("schema", ""));
        return Headers.of(headers);
    }

    /**
     * Update SharedPreferences
     * @param toEdit
     * @param tag
     * @param value
     */
    public static void updateSharedPreferences(SharedPreferences.Editor toEdit, String tag, String value) {
        toEdit.putString(tag, value);
        toEdit.commit();
    }

    /**
     * Force Logout User
     * @param toEdit
     * @param activity
     * @param message
     */
    public static void forceLogoutUser(SharedPreferences.Editor toEdit, Activity activity, String message, SharedPreferences sh_Pref) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(activity.getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            dialog.dismiss();
                            userLogOut(toEdit, activity, sh_Pref);
                        });
                //.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Logout
     * @param toEdit
     * @param activity
     */
    public static void userLogOut(SharedPreferences.Editor toEdit, Activity activity, SharedPreferences sh_Pref) {
        String logoPath = sh_Pref.getString(AppConst.COLLEGE_LOGO, "");
        String schema = sh_Pref.getString(AppConst.SCHEMA, "");
        toEdit.clear().commit();
        //adding logo once again and deleting other values
        toEdit.putString(AppConst.COLLEGE_LOGO, logoPath);
        toEdit.putString(AppConst.SCHEMA, schema);
        toEdit.apply();
        Intent loginIntent = new Intent(activity, UserSelection.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(loginIntent);
        activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        activity.finish();
    }

    //logic to show logout alert
    public static boolean showLogOutAlert(JSONObject parentjObject) throws JSONException {
        return parentjObject.getString(AppConst.STATUS_CODE).equalsIgnoreCase(AppConst.NOT_AUTHORIZED_TO_LOGIN) ||
                parentjObject.getString(AppConst.STATUS_CODE).equalsIgnoreCase(AppConst.TOKEN_NOT_EXIST);
    }

    //check whether content is having access or not
    public boolean getAccessType(String chapterTopicAccessId, String access) {
        return chapterTopicAccessId.equalsIgnoreCase("0") && access.equalsIgnoreCase("1");
    }

    //add rupee symbol
    public String addRupeeSymbol(Object val){
        return "\u20B9 "+ val;
    }

    //update date format
    public String convertStrtoStrDts(String cDtFmt, String convertDtFmt, String dtString){
        String result = "";
        try {
            Date dt = new SimpleDateFormat(cDtFmt, Locale.getDefault()).parse(dtString);
            if(dt != null)
            result = new SimpleDateFormat(convertDtFmt, Locale.getDefault()).format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
