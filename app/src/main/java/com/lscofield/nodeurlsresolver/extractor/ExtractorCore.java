package com.lscofield.nodeurlsresolver.extractor;

import android.content.Context;

public class ExtractorCore {
    public static String getFinalURL(String url, Context context){
        String finalURL = null;
        if (url.contains("clipwatching"))
            finalURL = Clipwatching.getFasterLink(url, context);
        else if (url.contains("cloudvideo"))
            finalURL = Cloudvideo.getFasterLink(url, context);
        else if (url.contains("dood"))
            finalURL = Dood.getFasterLink(url, context);
        else if (url.contains("fembed"))
            finalURL = Fembed.getFasterLink(url, context);
        else if (url.contains("jawcloud"))
            finalURL = Jawcloud.getFasterLink(url, context);
        else if (url.contains("jetload"))
            finalURL = Jetload.getFasterLink(url, context);
        else if (url.contains("mixdrop"))
            finalURL = Mixdrop.getFasterLink(url, context);
        else if (url.contains("mp4upload"))
            finalURL = Mp4upload.getFasterLink(url, context);
        else if (url.contains("openplay"))
            finalURL = Openplay.getFasterLink(url, context);
        else if (url.contains("prostream"))
            finalURL = Prostream.getFasterLink(url, context);
        else if (url.contains("streamtape"))
            finalURL = Streamtape.getFasterLink(url, context);
        else if (url.contains("supervideo"))
            finalURL = Supervideo.getFasterLink(url, context);
        else if (url.contains("upstream"))
            finalURL = Upstream.getFasterLink(url, context);
        else if (url.contains("uptostream"))
            finalURL = Uptostream.getFasterLink(url, context);
        else if (url.contains("uqload"))
            finalURL = Uqload.getFasterLink(url, context);
        else if (url.contains("veoh"))
            finalURL = Veoh.getFasterLink(url, context);
        else if (url.contains("vidcloud"))
            finalURL = Vidcloud.getFasterLink(url, context);
        else if (url.contains("videobin"))
            finalURL = Videobin.getFasterLink(url, context);
        else if (url.contains("videomega"))
            finalURL = Videomega.getFasterLink(url, context);
        else if (url.contains("vidfast"))
            finalURL = Vidfast.getFasterLink(url, context);
        else if (url.contains("vidia"))
            finalURL = Vidia.getFasterLink(url, context);
        else if (url.contains("vidlox"))
            finalURL = Vidlox.getFasterLink(url, context);
        else if (url.contains("vidoza"))
            finalURL = Vidoza.getFasterLink(url, context);
        else if (url.contains("vup"))
            finalURL = Vup.getFasterLink(url, context);

        return finalURL;
    }
}
