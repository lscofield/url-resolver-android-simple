package com.lscofield.nodeurlsresolver.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.DefaultDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class ExoPlayerCache {
    private static SimpleCache cache;

    public static SimpleCache getInstance(Context ctx){
        DatabaseProvider dp =
                new DefaultDatabaseProvider(
                        new SQLiteOpenHelper(
                                ctx,
                                "ExoPlayer",
                                null,
                                1) {
            @Override
            public void onCreate(SQLiteDatabase db) { }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        });

        if (cache == null) {
            cache = new SimpleCache(
                    new File(ctx.getCacheDir(),
                            "ExoPlayerCache"),
                    new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024),
                    dp);
           /* cache = new SimpleCache(
                    new File(ctx.getCacheDir(),
                    "ExoPlayerCache"),
                    new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));*/
        }

        return cache;
    }
}
