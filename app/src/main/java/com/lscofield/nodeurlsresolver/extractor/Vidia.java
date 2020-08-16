package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.SCheck;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Vidia {
    public static String getFasterLink(String l, Context ctx) {
        String authJSON = SCheck.getCheckString(ctx);
        String mp4 = null;
        try{
            //
            String apiURL = Conses.API_EXTRACTOR + "vidia";
            String obj = Jsoup.connect(apiURL)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .userAgent("Mozilla")
                    .data("source", Utils.encodeMSG(l))
                    .data("auth", Utils.encodeMSG(authJSON))
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute().body();

            if(obj != null && obj.contains("url")){
                JSONObject json = new JSONObject(obj);

                if (json.getString("status").equals("ok"))
                    mp4 = json.getString("url");
            }
        }catch (Exception er){
        }

        return mp4;
    }

}
