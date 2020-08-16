package com.lscofield.nodeurlsresolver.services;

import android.content.Context;
import android.content.Intent;

import ir.siaray.downloadmanagerplus.receivers.NotificationBroadcastReceiver;

public class DownloaderReceiver extends NotificationBroadcastReceiver {
    @Override
    public void onCompleted(Context context, Intent intent, long downloadId) {
        super.onCompleted(context, intent, downloadId);
    }

    @Override
    public void onClicked(Context context, Intent intent, long[] downloadIdList) {
        super.onClicked(context, intent, downloadIdList);
    }

    @Override
    public void onFailed(Context context, Intent intent, long downloadId) {
        super.onFailed(context, intent, downloadId);
    }
}