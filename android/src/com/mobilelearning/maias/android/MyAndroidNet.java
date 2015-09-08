package com.mobilelearning.maias.android;

/**
 * Created by AFFonseca on 17/05/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import com.badlogic.gdx.backends.android.AndroidNet;
import com.badlogic.gdx.net.NetJavaServerSocketImpl;
import com.badlogic.gdx.net.NetJavaSocketImpl;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.mobilelearning.maias.serviceHandling.SSLv3Fix.MyNetJavaImpl;

/** Android implementation of the {@link Net} API.
 * @author acoppes */
public class MyAndroidNet extends AndroidNet {

    // IMPORTANT: The Gdx.net classes are a currently duplicated for JGLFW/LWJGL + Android!
    // If you make changes here, make changes in the other backend as well.
    final AndroidApplicationBase app;
    MyNetJavaImpl netJavaImpl;

    public MyAndroidNet(AndroidApplicationBase app) {
        super(app);

        this.app = app;
        netJavaImpl = new MyNetJavaImpl();

    }

    @Override
    public void sendHttpRequest (HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
        netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    @Override
    public void cancelHttpRequest (HttpRequest httpRequest) {
        netJavaImpl.cancelHttpRequest(httpRequest);
    }

    @Override
    public ServerSocket newServerSocket (Protocol protocol, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, port, hints);
    }

    @Override
    public Socket newClientSocket (Protocol protocol, String host, int port, SocketHints hints) {
        return new NetJavaSocketImpl(protocol, host, port, hints);
    }

    @Override
    public boolean openURI (String URI) {
        boolean result = false;
        final Uri uri = Uri.parse(URI);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager pm = app.getContext().getPackageManager();
        if (pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            app.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    // LiveWallpaper and Daydream applications need this flag
                    if (!(app.getContext() instanceof Activity))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    app.startActivity(intent);
                }
            });
            result = true;
        }
        return result;
    }

}