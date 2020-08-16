package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.SCheck;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Veoh {
    public static String getFasterLink(String l, Context ctx) {
        String authJSON = SCheck.getCheckString(ctx);
        String document = null;
        String mp4 = null;
        try {
            String video = l.contains("/getVideo/") ? l : "https://www.veoh.com/watch/getVideo/" + l.split("/")[4];
            document = Jsoup.connect(video)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .userAgent("Mozilla")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute().body();

            try{
                //
                String apiURL = Conses.API_EXTRACTOR + "veoh";
                String obj = Jsoup.connect(apiURL)
                        .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                        .data("mode", "local")
                        .data("auth", Utils.encodeMSG(authJSON))
                        .data("source", Utils.encodeMSG(document))
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

        } catch (Exception e) {

        }

        return mp4;
    }
}
