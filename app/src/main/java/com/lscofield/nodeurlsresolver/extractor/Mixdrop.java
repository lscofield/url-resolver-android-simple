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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mixdrop {
    public static String getFasterLink(String l, Context ctx){
        String authJSON = SCheck.getCheckString(ctx);
        l = l.replace("/f/", "/e/");
        Document document = null;
        String headers = "Referer@" + l;
        Map<String, String> mapHeaders = new HashMap<>();
        ArrayList<String> hd = new ArrayList<>(Arrays.asList(headers.split("@")));

        for (int i = 0; i < hd.size(); i++){
            if(i % 2 == 0)
                mapHeaders.put(hd.get(i), hd.get(i+1));
        }

        String mp4 = null;
        try {
            document = Jsoup.connect(l)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .headers(mapHeaders)
                    .parser(Parser.htmlParser()).get();

            if (document == null || !document.toString().contains("eval(")){
                if (document != null){
                    Pattern p = Pattern.compile("window.location\\s*=\\s*\"(.*?)\"", Pattern.DOTALL);
                    Matcher m = p.matcher(document.toString());

                    if(m.find()) {
                        String token = m.group(1);
                        if (token != null && !token.isEmpty()){
                            l = l.split("/e/")[0] + token;
                            document = Jsoup.connect(l)
                                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                                    .headers(mapHeaders)
                                    .parser(Parser.htmlParser()).get();
                        }
                    }
                }
            }

            try{
                //
                String apiURL = Conses.API_EXTRACTOR +"mixdrop";
                String obj = Jsoup.connect(apiURL)
                        .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                        .data("source", Utils.encodeMSG(document.toString()))
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
        } catch (Exception e) {}

        return mp4;
    }
}
