package com.lscofield.nodeurlsresolver.util;

import android.content.Context;
import com.github.javiersantos.piracychecker.utils.LibraryUtilsKt;

public class SCheck {
    public static String getCheckString(Context ctx){
        String skk = LibraryUtilsKt.getApkSignatures(ctx)[0];
        String auth = "";
        return "{\"skk\":\""+skk+"\",\"auth\":\""+auth+"\"}";
    }
}
