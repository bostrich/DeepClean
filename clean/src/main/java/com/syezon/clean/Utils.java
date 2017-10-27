package com.syezon.clean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.text.DecimalFormat;

/**
 *
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * 通过包名，判断某应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkInstall(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "checkInstall e:" + e.toString());
            return false;
        }
    }


    public static int getApkVersionCode(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return 0;
            } else {
                return packageInfo.versionCode;
            }
        } catch (Exception e) {
            Log.e(TAG, "checkInstall e:" + e.toString());
            return 0;
        }
    }

    public static String formatSize(long size){
        double total = size / 1024.0;
        String unit = "";
        if(total > 1024){
            unit = "MB";
            total /= 1024.0;
            if(total > 1000){
                total /= 1024.0;
                unit = "GB";
            }
        }else{
            unit = "kB";
        }
        DecimalFormat format = new DecimalFormat("#0");
        if (total < 10) {
            format = new DecimalFormat("#0.00");
        } else if (total < 100) {
            format = new DecimalFormat("#0.0");
        }
        return format.format(total) + unit;
    }

}
