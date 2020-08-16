package com.lscofield.nodeurlsresolver.util;

import java.util.Arrays;
import java.util.List;

public class Conses {
    public static final String APP_NAME = "Url Resolver Simple App";
    ///End points
    public static String API_EXTRACTOR = "https://your-urlresolver-api-domain/api/v1/";

    public static List<String> SUPPORTED_HOSTS =
            Arrays.asList(
                    "clipwatching",
                    "cloudvideo",
                    "dood",
                    "fembed",
                    "jawcloud",
                    "jetload",
                    "mixdrop",
                    "mp4upload",
                    "openlay",
                    "prostream",
                    "streamtape",
                    "supervideo",
                    "upstream",
                    "uptostream",
                    "uqload",
                    "veoh",
                    "vidcloud",
                    "videobin",
                    "videomega",
                    "vidfast",
                    "vidia",
                    "vidlox",
                    "vidoza",
                    "vup"
            );
    public static List<String> DOWNLOADABLE_HOSTS =
            Arrays.asList(
                    "clipwatching",
                    "cloudvideo",
                    "fembed",
                    "jawcloud",
                    "jetload",
                    "mp4upload",
                    "openlay",
                    "prostream",
                    "streamtape",
                    "supervideo",
                    "upstream",
                    "uptostream",
                    "uqload",
                    "veoh",
                    "vidcloud",
                    "videobin",
                    "videomega",
                    "vidfast",
                    "vidia",
                    "vidlox",
                    "vidoza",
                    "vup"
            );
    /// Timeout
    public static final int EXO_CON_TIME = 60000;
    public static final int TIMEOUT_EXTRACT_MILS = 20000;
    public static final int TIMEOUT_SECONDS = 8;
}
