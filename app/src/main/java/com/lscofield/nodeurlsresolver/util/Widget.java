package com.lscofield.nodeurlsresolver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;

import com.lscofield.nodeurlsresolver.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Widget {
    /**
     * Notify alert dialog success
     * @param msg
     */
    public static void notify(String msg, Context mContext, int errorType){
        new SweetAlertDialog(mContext, errorType)
                .setTitleText("")
                .setConfirmButtonBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                .setContentText(msg)
                .show();
    }

    public static String getDataPref(Context ctx, String key){
        try{
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
            return SP.getString(key, "");
        }catch (Exception e){
            return "";
        }
    }

    public static void setAdsInitializeStatus(String status, Context mContext) {
        putDataPref(mContext, "ad_init_status", status);
    }

    public static boolean getAdsInitialized(Context mContext) {
        return !getDataPref(mContext, "ad_init_status").isEmpty();
    }

    public  static void putDataPref(Context ctx, String key, String value){
        try{
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
            editor.putString(key, value);
            editor.apply();
        }catch (Exception exx){

        }
    }

    public static boolean canPIP(Context ctx){
        boolean can = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && ctx.getPackageManager()
                .hasSystemFeature(
                        PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            can = true;
        }

        return can;
    }
}
