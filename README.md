Here is an Android Simple APP make with java to see the power of node-urlresolver-api

App integrated with:
- A video player (exoplayer based)
    - Pinch zoom in/out support
    - Play local files support
    - Play hls.m3u8 support
    - Open from intent like standard video player
- Google Admob integrated
- A download manager with pause/resume/delete/open support

- Here is the apk file [download link](http://www.mediafire.com/folder/ur0h8u9t90enu/nide-url-resolver)
- Here is the repository contains [code of resolver api](https://github.com/lscofield/node-urlresolver-api)


- Screenshots

![](https://i.imgur.com/2Xr1dYR.jpg)




![](https://i.imgur.com/sSEANNG.jpg)



- Steps to customize and compile App:
    - Refactor package
    - Set your resolver api domain on [app-package].util.Conses.java
    - Set your Admob ads/app IDs on string.xml
    - [optional] set your device id to see Google test Ads on  [app-package].util.Utils.java (setTestAdsEnabled method)
    - Compile and enjoy!!
