package com.lscofield.nodeurlsresolver.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Rational;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.DefaultDatabaseProvider;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.snackbar.Snackbar;
import com.lscofield.nodeurlsresolver.R;
import com.lscofield.nodeurlsresolver.data.ExoPlayerCache;
import com.lscofield.nodeurlsresolver.services.PinchListener;
import com.lscofield.nodeurlsresolver.util.Conses;
import com.lscofield.nodeurlsresolver.util.Utils;
import com.lscofield.nodeurlsresolver.util.Widget;
import com.lscofield.nodeurlsresolver.widget.CustomOnScaleGestureListener;
import com.lscofield.nodeurlsresolver.widget.PlayerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

public class VideoPlayer extends AppCompatActivity {
    private Context mContext;

    /// Player vars
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ActionBar ac;
    private static boolean floating = false;
    private TextView tv;
    private TrailingCircularDotsLoader mLoading;
    private Toolbar toolbar;
    private Boolean isPreparing = false;
    private ImageView mExoRewind, mExoForward, mExoLock;
    private LinearLayout mExoControls1, mExoControls2, mExoControls3;
    private boolean stream = true, locked = false;
    private ScaleGestureDetector scaleGestureDetector;

    // Ads vars
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    /// Source data
    private boolean isPremium = false;
    private String url, title, referer;

    /// PIP mode
    private BroadcastReceiver mReceiver;
    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        if (Utils.isEmulator())
            finishAndRemoveTask();
        // Set context
        mContext = VideoPlayer.this;

        Utils.initializeAds(mContext);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        zoomVideo();

        retrieveExtras();

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setVisibility(View.VISIBLE);

        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.ADMOB_INTERSTITIAL_LOCAL));

        // Link xml elements
        toolbar = findViewById(R.id.toolbar);
        mLoading = findViewById(R.id.loadIndicator);
        playerView = findViewById(R.id.playerView);
        mExoForward = findViewById(R.id.exo_ffwd);
        mExoRewind = findViewById(R.id.exo_rew);
        mExoLock = findViewById(R.id.exo_lock);
        mExoControls1 = findViewById(R.id.exo_controls1);
        mExoControls2 = findViewById(R.id.dura_els);
        mExoControls3 = findViewById(R.id.prog_els);
        scaleGestureDetector = new ScaleGestureDetector(mContext,
                new CustomOnScaleGestureListener(new PinchListener() {
                    @Override
                    public void onZoomOut() {
                        if (!locked && playerView != null) {
                            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                            showSnack(getString(R.string.zoom_out));
                        }
                    }

                    @Override
                    public void onZoomIn() {
                        if (!locked && playerView != null) {
                            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                            showSnack(getString(R.string.zoom_in));
                        }
                    }
                }));

        // Set toolbar
        setSupportActionBar(toolbar);
        startPlaying(url);
    }

    private void showInterstitial(){
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                player.setPlayWhenReady(true);
            }

            @Override
            public void onAdOpened() {
                player.setPlayWhenReady(false);
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                player.setPlayWhenReady(true);
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showSnack(String msg){
        CoordinatorLayout coordinatorLayout=(CoordinatorLayout)findViewById(R.id.cordinator);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#65000000"));

        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
        snackbar.show();
    }

    private void startPlaying(String finalURL){
        url = finalURL;
        openPlayer();
    }

    private void retrieveExtras(){
        try {
            url = getIntent().getStringExtra("url");
            referer = getIntent().getStringExtra("referer");
            title = getIntent().getStringExtra("title");
            stream = getIntent().getBooleanExtra("stream", false);

            if (url == null || title == null){
                referer = "";
                url = getIntent().getDataString();
                stream = !(url.startsWith("content:") || url.startsWith("file:"));
                title = "video_" + Calendar.getInstance().getTimeInMillis();
            }
        }catch (Exception er){
            Toast.makeText(mContext, getString(R.string.error_ocurr), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //// METHODS
    // Init player
    private void openPlayer() {
        // Set increment forward/rewind
        playerView.setFastForwardIncrementMs(10000);
        playerView.setRewindIncrementMs(10000);

        // Lock button listener
        mExoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locked = !locked;
                mExoLock.setImageDrawable(ContextCompat.getDrawable(mContext,
                        locked ? R.drawable.ic_lock_open_outline : R.drawable.ic_lock_closed_outline));
                VideoPlayer.this.lockPlayerControls(locked);
            }
        });

        setActionBar();
        try{
            changeBarSize();
        }catch (Exception tr){}
        init(false);
    }

    /**
     * Set toolbar
     */
    private void setActionBar(){
        ac = getSupportActionBar();
        if (ac != null) {
            ac.setHomeAsUpIndicator(R.drawable.ic_back_trim);
            ac.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Change toolbar size
     */
    private void changeBarSize(){
        tv = new TextView(mContext);

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(lp);
        tv.setText(getString(R.string.m_load));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);

        ac.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ac.setCustomView(tv);
        ac.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Lock/unlock ads
     * @param lock
     */
    public void toggleBanner(boolean lock){
        mAdView.setVisibility(lock ? View.INVISIBLE : (!isPremium ? View.VISIBLE : View.INVISIBLE));
    }

    /**
     * Lock unlock screen controls
     * @param lock
     */
    private void lockPlayerControls(boolean lock){
        toggleBanner(lock);
        mExoControls1.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        mExoControls2.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        mExoControls3.setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        showActionBar(!lock);
    }

    /**
     * Show hide toolbar
     * @param show
     */
    private void showActionBar(boolean show){
        if (ac != null)
            if (show){
                if (!ac.isShowing()){
                    ac.show();
                }
            }else{
                if (ac.isShowing()){
                    ac.hide();
                }
            }
    }

    /**
     * Zoom video to fit
     */
    private void zoomVideo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Window window =  this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getAttributes().layoutInDisplayCutoutMode =  WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    /**
     * Show progress bar
     * @param show
     */
    private void showLoading(boolean show){
        if(mLoading != null){
            if (show){
                if (mLoading.getVisibility() != View.VISIBLE){
                    mLoading.setVisibility(View.VISIBLE);
                }
            }else{
                mLoading.setVisibility(View.GONE);
            }
        }
    }

    /// PLAYER METHODS
    private void init(Boolean all) {
        showLoading(true);
        DefaultLoadControl.Builder bl = new DefaultLoadControl.Builder();
        bl.setBufferDurationsMs(3500,
                150000,
                2500,
                3000);

        SimpleExoPlayer.Builder builder = new SimpleExoPlayer.Builder(
                mContext,
                new DefaultRenderersFactory(mContext)
        ).setLoadControl(bl.createDefaultLoadControl());

        player = builder.build();
        player.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        playerView.setPlayer(player);
        playerView.setGestureDetector(scaleGestureDetector);
        playerView.setKeepScreenOn(true);
        playerView.requestFocus();
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    if (!locked)
                        VideoPlayer.this.showActionBar(true);
                } else {
                    VideoPlayer.this.showActionBar(false);
                }
            }
        });

        MediaSource mediaSource;

        if(stream){
            Map<String, String> headers = new HashMap<>();
            headers.put("Referer", referer);

            try{
                DefaultHttpDataSourceFactory dataIntance = new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(mContext, this.getPackageName()),
                        Conses.EXO_CON_TIME,
                        Conses.EXO_CON_TIME,
                        true
                );

                DatabaseProvider dp =
                        new DefaultDatabaseProvider(
                                new SQLiteOpenHelper(
                                        mContext,
                                        "ExoPlayer",
                                        null,
                                        1) {
                                    @Override
                                    public void onCreate(SQLiteDatabase db) {

                                    }

                                    @Override
                                    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                                    }
                                });
                dataIntance.getDefaultRequestProperties().set(headers);
                CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(
                        ExoPlayerCache.getInstance(mContext), dataIntance, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                switch (Util.inferContentType(Uri.parse(url))){
                    case C.TYPE_HLS:
                        mediaSource = new HlsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_SS:
                        mediaSource = new SsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_DASH:
                        mediaSource = new DashMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    default:
                        mediaSource = new ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                }
            }catch (Exception err){
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(mContext, this.getPackageName()),
                        Conses.EXO_CON_TIME,
                        Conses.EXO_CON_TIME,
                        true
                );

                dataSourceFactory.getDefaultRequestProperties().set(headers);

                switch (Util.inferContentType(Uri.parse(url))){
                    case C.TYPE_HLS:
                        mediaSource = new HlsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_SS:
                        mediaSource = new SsMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    case C.TYPE_DASH:
                        mediaSource = new DashMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                    default:
                        mediaSource = new ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(url));
                        break;
                }
            }
        }else{
            DefaultDataSourceFactory dataSourceFactory =
                    new DefaultDataSourceFactory(this,
                            Util.getUserAgent(this, this.getPackageName()));

            mediaSource = new ProgressiveMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(url));
        }

        isPreparing = true;
        player.prepare(mediaSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        showLoading(true);
                        break;
                    case Player.STATE_ENDED:
                        showLoading(false);
                        break;
                    case Player.STATE_IDLE:
                        showLoading(true);
                        break;
                    case Player.STATE_READY:
                        showLoading(false);
                        if (isPreparing){
                            showLoading(false);
                            isPreparing = false;
                            showInterstitial();
                        }
                        break;
                    default:
                        showLoading(false);
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
        player.setPlayWhenReady(true);
        MediaSessionCompat mediaSession = new MediaSessionCompat(mContext, getPackageName());
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        mediaSession.setActive(true);
        tv.setText(title);
        if (!playerView.getUseController())
            playerView.setUseController(true);
    }

    private void releasePlayer(Boolean finish) {
        if (player != null) {
            if (finish) {
                mLoading = null;
                player.release();
                playerView.setPlayer(null);
                finish();
            } else pausePlayer();
        }else{
            finish();
        }
    }

    @SuppressLint("NewApi")
    private void enterPIPMode() {
        if (Widget.canPIP(mContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Rational aspectRatio = new Rational(playerView.getWidth(), playerView.getHeight());
                PictureInPictureParams.Builder params = new PictureInPictureParams.Builder();
                params.setAspectRatio(aspectRatio).build();
                this.enterPictureInPictureMode(params.build());
            } else {
                this.enterPictureInPictureMode();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void createPipAction() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final ArrayList<RemoteAction> actions = new ArrayList<>();

            Intent actionIntent =
                    new Intent("com.lscofield.nodeurlsresolver.PLAY_PAUSE");

            final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    REQUEST_CODE, actionIntent, 0);
            Icon icon = Icon.createWithResource(mContext,
                    player != null && player.getPlayWhenReady() ? R.drawable.ic_pause_outline : R.drawable.ic_play_outline );
            icon.setTint(ContextCompat.getColor(mContext, R.color.backWhite));

            RemoteAction remoteAction = new RemoteAction(icon, "Player",
                    "Play", pendingIntent);

            actions.add(remoteAction);
            PictureInPictureParams params =
                    new PictureInPictureParams.Builder()
                            .setActions(actions)
                            .build();

            setPictureInPictureParams(params);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if ((keyCode == KeyEvent.KEYCODE_BACK ||
                keyCode == KeyEvent.KEYCODE_MENU ||
                keyCode == KeyEvent.KEYCODE_HOME) && locked) {
            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPictureInPictureModeChanged (boolean isInPictureInPictureMode,
                                               Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            startPlayer();
            playerView.setUseController(false);
            floating = true;
            showActionBar(false);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.lscofield.nodeurlsresolver.PLAY_PAUSE");
                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context,
                                          Intent intent) {
                        if (player != null){
                            boolean state = !player.getPlayWhenReady();
                            player.setPlayWhenReady(state);
                            createPipAction();
                        }
                    }
                };
                registerReceiver(mReceiver, filter);
                createPipAction();
            }
        } else {
            playerView.setUseController(true);
            floating = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mReceiver != null) {
                    unregisterReceiver(mReceiver);
                }
            }
        }
    }


    private void pausePlayer(){
        if (player != null){
            try{
                if (player.getPlaybackState() == Player.STATE_READY
                        && player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }catch (Exception e){}
        }
    }

    private void startPlayer(){
        if (player != null){
            try{
                if (player.getPlaybackState() == Player.STATE_READY
                        && !player.getPlayWhenReady()){
                    player.setPlayWhenReady(true);
                }
            }catch (Exception u){}
        }
    }

    @Override
    public void onPause() {
        showActionBar(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode() && floating) {
            releasePlayer(false);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode()){
        }else{
            pausePlayer();
        }

        super.onPause();
    }


    @Override
    public void onStop() {
        if (floating)
            releasePlayer(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode() && floating) {
            releasePlayer(false);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                isInPictureInPictureMode()){

        }else{
            pausePlayer();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        releasePlayer(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlayer();
    }

    @Override
    public void onBackPressed() {
        releasePlayer(true);
        super.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        try {
            enterPIPMode();
        }catch (Exception e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
