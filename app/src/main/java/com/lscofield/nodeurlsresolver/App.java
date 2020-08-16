package com.lscofield.nodeurlsresolver;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.gms.security.ProviderInstaller;
import com.lscofield.nodeurlsresolver.services.NoSSLv3SocketFactory;

import java.net.URISyntaxException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
