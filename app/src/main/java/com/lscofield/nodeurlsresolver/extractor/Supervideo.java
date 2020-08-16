package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.SCheck;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class Supervideo {
    public static String getFasterLink(String l, Context ctx){
        String authJSON = SCheck.getCheckString(ctx);
        Document document = null;
        String mp4 = null;
        String apiURL = Conses.API_EXTRACTOR + "supervideo";
        try {
            document = Jsoup.connect(l)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .userAgent("Mozilla")
                    .parser(Parser.htmlParser()).get();

            try{
                //
                String obj = Jsoup.connect(apiURL)
                        .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                        .data("source", Utils.encodeMSG(document.toString()))
                        .data("auth", Utils.encodeMSG(authJSON))
                        .data("mode", "local")
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

            if(mp4 == null || mp4.isEmpty()){
                try{
                    String obj = Jsoup.connect(apiURL)
                            .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                            .data("source", Utils.encodeMSG(l))
                            .data("auth", Utils.encodeMSG(authJSON))
                            .data("mode", "remote")
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
            }
        } catch (Exception e) {
        }


        return mp4;
    }
}
