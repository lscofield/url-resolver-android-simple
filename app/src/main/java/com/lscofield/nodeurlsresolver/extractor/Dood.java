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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dood {
    public static String getFasterLink(String l, Context ctx) {
        String authJSON = SCheck.getCheckString(ctx);
        Document document = null;
        String mp4 = null;
        l = l.replace("/e/", "/d/");
        String embedRegex = "\\/pass_md5\\/(.*?)[\'|\"]";
        String downloadRegex = "\\/download\\/(.*?)[\'|\"]";


        try {
            String apiURL = Conses.API_EXTRACTOR + "dood";
            // try with download mode
           try {
                document = Jsoup.connect(l)
                        .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                        .userAgent("Mozilla")
                        .referrer(l)
                        .parser(Parser.htmlParser()).get();

                Pattern p = Pattern.compile(downloadRegex, Pattern.DOTALL);
                Matcher m = p.matcher(document.toString());
                if (m.find()) {
                    String downloadLink = "https://dood.watch/download/" + m.group(1);

                    document = Jsoup.connect(downloadLink)
                            .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                            .userAgent("Mozilla")
                            .referrer(l)
                            .parser(Parser.htmlParser()).get();

                    try {
                        //
                        String obj = Jsoup.connect(apiURL)
                                .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                                .data("mode", "direct")
                                .data("auth", Utils.encodeMSG(authJSON))
                                .data("source", Utils.encodeMSG(document.toString()))
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
                }
            }catch (Exception tr){
            }

            if (mp4 == null || mp4.isEmpty()){
                // try with embed mode
                l = l.replace("/d/","/e/").replace("/h/", "/e/");
                try {
                    document = Jsoup.connect(l)
                            .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                            .userAgent("Mozilla")
                            .referrer(l)
                            .parser(Parser.htmlParser()).get();

                    Pattern p = Pattern.compile(embedRegex, Pattern.DOTALL);
                    Matcher m = p.matcher(document.toString());
                    if (m.find()) {
                        String pasrs = m.group(1);
                        String embedLink = "https://dood.watch/pass_md5/" + pasrs;

                        document = Jsoup.connect(embedLink)
                                .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                                .userAgent("Mozilla")
                                .referrer(l)
                                .parser(Parser.htmlParser()).get();

                        try {
                            //
                            String obj = Jsoup.connect(apiURL)
                                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                                    .data("mode", "embed")
                                    .data("token", pasrs)
                                    .data("auth", Utils.encodeMSG(authJSON))
                                    .data("source", Utils.encodeMSG(document.toString()))
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
                    }
                }catch (Exception tr){
                }
            }
        } catch (Exception e) {
        }

        return mp4;
    }
}
