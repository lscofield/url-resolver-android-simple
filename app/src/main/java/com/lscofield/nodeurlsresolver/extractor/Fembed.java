package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;


import com.lscofield.nodeurlsresolver.util.Conses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Fembed {
    public static String getFasterLink(String l, Context ctx) {
        String mp4 = null;
        String file = l.split("/")[4];
        try{
            //
            String apiURL = "https://feurl.com/api/source/" + file;
            String obj = Jsoup.connect(apiURL)
                    .timeout(Conses.TIMEOUT_EXTRACT_MILS)
                    .data("r", "")
                    .data("d", "feurl.com")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute().body();


            try{
                JSONObject json = new JSONObject(obj);
                if(json.getBoolean("success") && obj.contains("mp4")){
                    JSONArray jsonArray = json.getJSONArray("data");
                    mp4 = jsonArray.getJSONObject(0).getString("file");
                }
            }catch (JSONException e){}
        }catch (Exception er){
        }
        return mp4;
    }
}
