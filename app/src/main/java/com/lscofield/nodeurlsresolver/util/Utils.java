package com.lscofield.nodeurlsresolver.util;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.lscofield.nodeurlsresolver.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
    /**
     * Return is supported host
     * @param url
     * @return
     */
    public static boolean isSupportedHost(String url){
        for (String host : Conses.SUPPORTED_HOSTS)
            if (url.contains(host))
                return true;
        return false;
    }

    /**
     * Return is supported host
     * @param url
     * @return
     */
    public static boolean isDownloadableHost(String url){
        for (String host : Conses.DOWNLOADABLE_HOSTS)
            if (url.contains(host))
                return true;
        return false;
    }

    /**
     * Return encoded string to base64
     * @param msg
     * @return
     */
    public static String encodeMSG(String msg){
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Encode url string
     * @param s
     * @return
     */
    private  static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Encode url map
     * @param map
     * @return
     */
    public static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();
        } catch (Exception e) {
            return encodeMSG(s);
        }
    }

    public static boolean isEmulator() {
       return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    public static void setTestAdsEnabled(boolean enabled){
        if (enabled){
            List<String> testDeviceIds = Arrays.asList("TEST_DEVICE_ID");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }
    }

    public static void initializeAds(final Context mContext){
        if (BuildConfig.DEBUG) {
            setTestAdsEnabled(true);
        }else{
            if (!Widget.getAdsInitialized(mContext))
                MobileAds.initialize(mContext,
                        initializationStatus -> Widget.setAdsInitializeStatus("ok", mContext));
        }
    }
}
