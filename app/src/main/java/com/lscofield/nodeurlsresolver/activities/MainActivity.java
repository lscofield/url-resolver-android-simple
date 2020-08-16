package com.lscofield.nodeurlsresolver.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.lscofield.nodeurlsresolver.R;
import com.lscofield.nodeurlsresolver.adapters.DownloadsAdapter;
import com.lscofield.nodeurlsresolver.extractor.ExtractorCore;
import com.lscofield.nodeurlsresolver.types.UrlMode;
import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.Utils;
import com.lscofield.nodeurlsresolver.util.Widget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.siaray.downloadmanagerplus.classes.Downloader;
import ir.siaray.downloadmanagerplus.model.DownloadItem;

public class MainActivity extends AppCompatActivity {

    // Ui vars
    private Context mContext;
    private RecyclerView mDownloads;
    private DownloadsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText mURLField;
    private String url, videoTitle = Conses.APP_NAME;
    private ImageButton mPlay, mDownload, mShare;
    private SweetAlertDialog pDialog;
    private Downloader mDownloader;
    private List<DownloadItem> mDataSet;

    // Update download list
    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 1000;

    // Ads vars
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.isEmulator())
            finishAndRemoveTask();

        mContext = MainActivity.this;

        isWriteGranted();
        isReadGranted();

        Utils.initializeAds(mContext);

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.ADMOB_INTERSTITIAL_LOCAL));

        mDownloads = (RecyclerView) findViewById(R.id.recycler_downloads);
        mURLField = (EditText) findViewById(R.id.url_field);
        mPlay = (ImageButton) findViewById(R.id.home_play);
        mDownload = (ImageButton) findViewById(R.id.home_download);
        mShare = (ImageButton) findViewById(R.id.home_open);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        mDownloads.setLayoutManager(layoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mDownloads.setHasFixedSize(true);

        /// Setting onclick for buttons
        mPlay.setOnClickListener(v -> startWork(UrlMode.PLAY));
        mDownload.setOnClickListener(v -> startWork(UrlMode.DOWNLOAD));
        mShare.setOnClickListener(v -> startWork(UrlMode.SHARE));
        settingDialog();

        // specify an adapter (see also next example)
        mDataSet = Downloader.getDownloadsList(mContext);
        mAdapter = new DownloadsAdapter(mDataSet, mContext);
        mDownloads.setAdapter(mAdapter);
        showInterstitial();
    }

    private void isReadGranted() {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);

    }

    private void isWriteGranted() {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    private void showInterstitial(){
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {   }

            @Override
            public void onAdOpened() {  }

            @Override
            public void onAdClicked() { }

            @Override
            public void onAdLeftApplication() { }

            @Override
            public void onAdClosed() {  }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Initialize extraction
     * @param mode
     */
    private void startWork(UrlMode mode) {
        url = mURLField.getText().toString();
        if (url.isEmpty())
            Widget.notify(mContext.getString(R.string.url_empty), mContext,  SweetAlertDialog.ERROR_TYPE);
        else{
            if (!Utils.isSupportedHost(url))
                Widget.notify(mContext.getString(R.string.not_supported_host), mContext,  SweetAlertDialog.WARNING_TYPE);
            else {
                String host = "";
                try {
                    URL uri = new URL(url);
                    host = uri.getHost();
                } catch (Exception e) { }

                videoTitle = host + "_" + Calendar.getInstance().getTimeInMillis();
                new MyTask(url, mode).execute();
            }
        }
    }

    /**
     * Show/hide progress dialog
     * @param show
     */
    private void showProgress(boolean show){
        if (show)
            pDialog.show();
        else pDialog.dismiss();
    }

    /**
     * Initialize progress dialog
     */
    private void settingDialog(){
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        pDialog.setTitleText(mContext.getString(R.string.m_load));
        pDialog.setCancelable(false);
    }

    private void continueWork(String finalURL, UrlMode mode){
        if (finalURL != null){
            switch (mode){
                case PLAY:
                    startActivity(
                            new Intent(mContext, VideoPlayer.class)
                                    .putExtra("title", videoTitle)
                                    .putExtra("url", finalURL)
                                    .putExtra("referer", url)
                                    .putExtra("stream", true));
                    break;
                case SHARE:
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(finalURL))
                                    .setDataAndType(Uri.parse(finalURL), "video/*"));
                    break;
                case DOWNLOAD:
                    if (!Utils.isDownloadableHost(url))
                        Widget.notify(mContext.getString(R.string.not_supported_host_dw), mContext, SweetAlertDialog.WARNING_TYPE);
                    else{
                        if (url.contains("clipwatching") || url.contains("videobin")){
                            finalURL = finalURL.endsWith("master.m3u8") ?
                                    finalURL.replace("/hls/", "/")
                                            .replace(",", "")
                                            .replace(".urlset/master.m3u8", "/v.mp4") : finalURL;
                        }

                        if (!finalURL.endsWith(".m3u8"))
                            startDownload(finalURL);
                        else Widget.notify(mContext.getString(R.string.not_supported_host_dw), mContext, SweetAlertDialog.WARNING_TYPE);
                    }
                    break;
            }
        }else Widget.notify(mContext.getString(R.string.extraction_error), mContext,  SweetAlertDialog.ERROR_TYPE);
    }

    private void startDownload(String finalURL) {
        String token = Utils.md5(url);
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        mDownloader = Downloader.getInstance(mContext)
                .setUrl(finalURL)
                .setToken(token)
                .setDestinationDir(dir, videoTitle.replaceAll("[\\\\/:*?\"<>|]", "") + "_.mp4")
                .setNotificationTitle(videoTitle)
                .setDescription(videoTitle);

        mDownloader.start();
        notifyDownloadStatusChanged();
    }

    private void notifyDownloadStatusChanged(){
        if (mAdapter != null && Downloader.getDownloadsList(mContext).size() > 0){
            mDataSet.clear();
            mDataSet.addAll(Downloader.getDownloadsList(mContext));
            Collections.sort(mDataSet, (d1, d2) -> d2.getToken().compareTo(d1.getToken()));
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Extraction async task
     */
    private class MyTask extends AsyncTask<String, String, String> {
        String finalURL = null, link;
        UrlMode mode;

        public MyTask(String link, UrlMode mode) {
            this.link = link;
            this.mode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                finalURL = ExtractorCore.getFinalURL(url, mContext);
            } catch (Exception e){ }
            return finalURL;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            continueWork(result, mode);
        }
    }

    @Override
    public void onResume() {
        if (mContext != null && handler != null){
            handler.postDelayed(runnable = () -> {
                handler.postDelayed(runnable, delay);
                notifyDownloadStatusChanged();
            }, delay);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}