package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.Utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jetload {
    public static String getFasterLink(String l, Context ctx) {
        l = l.replace("/p/", "/e/");
        String mp4 = null;

        String v = "", cb = "123456789", site_key = "6Lc90MkUAAAAAOrqIJqt4iXY_fkXb7j3zwgRGtUI",
                co = "aHR0cHM6Ly9qZXRsb2FkLm5ldDo0NDM.", sa = "secure_url",
                url = "https://www.google.com/recaptcha/api2/anchor?ar=1&k=" + site_key + "&co=" + co + "&hl=ro&v=" + v + "&size=invisible&cb=" + cb;
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "ro-RO,ro;q=0.8,en-US;q=0.6,en-GB;q=0.4,en;q=0.2");
        headers.put("referer", "https://jetload.net");

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


                p = Pattern.compile("\\/[p|e]\\/([a-zA-Z0-9_]+)", Pattern.DOTALL);
                m = p.matcher(l);
                if(m.find()) {
                    String streamid = m.group(1);

                    p = Pattern.compile("rresp\\s*\"\\s*,\\s*\"\\s*(.*?)\"", Pattern.DOTALL);
                    m = p.matcher(data);
                    if(m.find()) {
                        token = m.group(1);


                        params = new HashMap<>();
                        params.put("token", token);
                        params.put("stream_code", streamid);

                        String pars = "{\"token\":\""+token+"\",\"stream_code\":\""+streamid+"\"}";

                        headers = new HashMap<>();
                        headers.put("Referer", l);
                        headers.put("Connection", "keep-alive");
                        headers.put("Content-Length", String.valueOf(pars.length()));
                        headers.put("Content-Type", "application/json;charset=UTF-8");
                        headers.put("Accept-Encoding", "gzip, deflate, br");
                        headers.put("Accept", "application/json, text/plain, */*");
                        headers.put("Accept-Language", "res-ES,es;q=0.9,en-US;q=0.8,en-GB;q=0.7,en;q=0.6,fr;q=0.5,id;q=0.4");

                        data = Jsoup.connect("https://jetload.net/jet_secure")
                                .timeout(Conses.TIMEOUT_SECONDS*1000)
                                .headers(headers)
                                .method(Connection.Method.POST)
                                .requestBody(pars)
                                .ignoreContentType(true)
                                .execute().body();


                        if (data != null && !data.isEmpty())
                            mp4 = new JSONObject(data).getJSONObject("src").getString("src");
                    }
                }
            }
        } catch (Exception e) {
            mp4 = null;
        }

        return mp4;
    }

}
