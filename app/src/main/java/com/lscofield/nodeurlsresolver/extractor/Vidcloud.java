package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;


import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vidcloud {
    public static String getFasterLink(String l, Context ctx) {
        String mp4 = null;

        String v = "JZfekeK8w6ZlhLfH_ZyseSLX", cb = "ilzxej5hmdxe", site_key = "6LdqXa0UAAAAABc77NIcku_LdXJio9kaJVpYkgQJ",
                co = "aHR0cHM6Ly92aWRjbG91ZC5ydTo0NDM.", sa = "get_playerr",
                url = "https://www.google.com/recaptcha/api2/anchor?ar=1&k=" + site_key + "&co=" + co + "&hl=es&v=" + v + "&size=invisible&cb=" + cb;
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "ro-RO,ro;q=0.8,en-US;q=0.6,en-GB;q=0.4,en;q=0.2");
        headers.put("referer", "https://vidcloud.ru");

        try {
            String data = Jsoup.connect(url)
                    .timeout(Conses.TIMEOUT_SECONDS*1000)
                    .headers(headers)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute().body();

            Pattern p = Pattern.compile("recaptcha-token\"\\s*value\\s*=\\s*\"(.*?)\"", Pattern.DOTALL);
            Matcher m = p.matcher(data);
            if(m.find()) {
                String token = m.group(1);

                String url2 = "https://www.google.com/recaptcha/api2/reload?k="+site_key;
                Map<String, String> params = new HashMap<>();
                params.put("v", v);
                params.put("reason", "q");
                params.put("k", site_key);
                params.put("c", token);
                params.put("sa", sa);
                params.put("co", co);
                String post = Utils.urlEncodeUTF8(params);

                headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                headers.put("Referer", l);

                data = Jsoup.connect(url2 + '&' + post)
                        .timeout(Conses.TIMEOUT_SECONDS*1000)
                        .headers(headers)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute().body();

                p = Pattern.compile("\\/[v|embed]\\/([a-zA-Z0-9_]+)", Pattern.DOTALL);
                m = p.matcher(l);
                if(m.find()) {
                    String streamid = m.group(1);

                    p = Pattern.compile("rresp\\s*\"\\s*,\\s*\"\\s*(.*?)\"", Pattern.DOTALL);
                    m = p.matcher(data);
                    if(m.find()) {
                        token = m.group(1);

                        String page = l.contains("/embed") ? "embed" : "video";

                        headers = new HashMap<>();
                        headers.put("Referer", l);
                        headers.put("Connection", "keep-alive");
                        headers.put("Accept-Encoding", "gzip, deflate, br");
                        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
                        headers.put("Accept-Language", "res-ES,es;q=0.9,en-US;q=0.8,en-GB;q=0.7,en;q=0.6,fr;q=0.5,id;q=0.4");

                        data = Jsoup.connect("https://vidcloud.ru/player")
                                .timeout(Conses.TIMEOUT_SECONDS*1000)
                                .headers(headers)
                                .method(Connection.Method.GET)
                                .data("token", token)
                                .data("page", page)
                                .data("fid", streamid)
                                .ignoreContentType(true)
                                .execute().body();

                        if (data != null && !data.isEmpty()){
                            String dataJson = new JSONObject(data).getString("html");
                            Pattern pattern = Pattern.compile("sources\\s*=\\s*\\[(.*?)]");
                            Matcher matcher = pattern.matcher(dataJson);
                            dataJson = null;
                            while (matcher.find()) {
                                dataJson = matcher.group(1);
                            }

                            if (dataJson != null){
                                JSONArray arr = new JSONArray("[" + dataJson + "]");
                                for (int i = 0; i < arr.length(); i++){
                                    mp4 = arr.getJSONObject(i).getString("file");
                                    if(mp4.contains(".mp4"))
                                        break;
                                }

                                if (mp4 != null)
                                    mp4 = mp4.replace("\\", "");
                            }

                        }

                    }
                }
            }
        } catch (Exception e) {
            mp4 = null;
        }

        return mp4;
    }
}
