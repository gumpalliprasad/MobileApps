package myschoolapp.com.gsnedutech.Neet.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.text.DecimalFormat;

public class NeetUtil {

    /**
     * Method to check for the availability of the local data
     */

    public static boolean isFileDataAvailableOrNot(Context context, String path) {

        if (!new File(Environment.getExternalStorageDirectory() + path).exists()) {
            return false;
        } else
            return new File(Environment.getExternalStorageDirectory() + path).exists();

    }

    /**
     * Generic Alert Box to be called from anny Activity of the Project
     * we will be calling this function using func_id
     * func_id - 1 = Internet
     * func_id - 2 = Generic
     **/

//    Alert Dialogue Bix
    public void alertDialog(final int func_id, final Activity activity, String title, String msg, String pbtn, String nbtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(pbtn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (func_id == 1)
                                    activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                if (func_id == 2)
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
        AlertDialog alert = builder.create();
        alert.show();
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

}
