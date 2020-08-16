package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.SCheck;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Uptostream {
    public static String getFasterLink(String link, Context ctx){
        String authJSON = SCheck.getCheckString(ctx);
        String mp4 = null;
        link = link.replace("uptobox.com", "uptostream.com");
        String file = link.split("/")[3];
        String apiURL = "https://uptostream.com/api/streaming/source/get?token=null&file_code="+file;

        try {
            String document = Jsoup.connect(apiURL)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .referrer(link)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute().body();

            try {
                //
                apiURL = Conses.API_EXTRACTOR + "uptostream";
                String obj = Jsoup.connect(apiURL)
                        .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                        .data("auth", Utils.encodeMSG(authJSON))
                        .data("source", Utils.encodeMSG(document))
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute().body();

                if (obj != null && obj.contains("url")) {
                    JSONObject json = new JSONObject(obj);

                    if (json.getString("status").equals("ok"))
                        mp4 = json.getString("url");
                }
            } catch (Exception er) {
            }
        } catch (IOException e) {

        }

        return mp4;
    }
}
