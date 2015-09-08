package com.mobilelearning.maias.serviceHandling.SSLv3Fix;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/** Implements part of the {@link Net} API using {@link HttpURLConnection}, to be easily reused between the Android and Desktop
 * backends.
 * @author acoppes */
public class MyNetJavaImpl {

    static class HttpClientResponse implements Net.HttpResponse {
        private final HttpURLConnection connection;
        private HttpStatus status;

        public HttpClientResponse (HttpURLConnection connection) throws IOException {
            this.connection = connection;
            try {
                this.status = new HttpStatus(connection.getResponseCode());
            } catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }

        @Override
        public byte[] getResult () {
            InputStream input = getInputStream();
            try {
                return StreamUtils.copyStreamToByteArray(input, connection.getContentLength());
            } catch (IOException e) {
                return StreamUtils.EMPTY_BYTES;
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override
        public String getResultAsString () {
            InputStream input = getInputStream();

            // If the response does not contain any content, input will be null.
            if (input == null) {
                return "";
            }

            try {
                return StreamUtils.copyStreamToString(input, connection.getContentLength());
            } catch (IOException e) {
                return "";
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override
        public InputStream getResultAsStream () {
            return getInputStream();
        }

        @Override
        public HttpStatus getStatus () {
            return status;
        }

        @Override
        public String getHeader (String name) {
            return connection.getHeaderField(name);
        }

        @Override
        public Map<String, List<String>> getHeaders () {
            return connection.getHeaderFields();
        }

        private InputStream getInputStream () {
            try {
                return connection.getInputStream();
            } catch (IOException e) {
                return connection.getErrorStream();
            }
        }
    }

    private final ExecutorService executorService;
    final ObjectMap<Net.HttpRequest, HttpURLConnection> connections;
    final ObjectMap<Net.HttpRequest, Net.HttpResponseListener> listeners;

    public MyNetJavaImpl() {
        executorService = Executors.newCachedThreadPool();
        connections = new ObjectMap<Net.HttpRequest, HttpURLConnection>();
        listeners = new ObjectMap<Net.HttpRequest, Net.HttpResponseListener>();

        //Preparing the new NOSSLv3Socket
        try{
            SSLContext sslcontext = SSLContext.getInstance("TLSv1");

            sslcontext.init(null, null, null);
            SSLSocketFactory NoSSLv3Factory = new com.mobilelearning.maias.serviceHandling.SSLv3Fix.NoSSLv3SocketFactory(sslcontext.getSocketFactory());

            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

        }catch (Exception e){
            System.err.println("Could not set NOSSLV3Socket!");
        }
    }

    public void sendHttpRequest (final Net.HttpRequest httpRequest, final Net.HttpResponseListener httpResponseListener) {
        if (httpRequest.getUrl() == null) {
            httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }

        try {
            final String method = httpRequest.getMethod();
            URL url;

            if (method.equalsIgnoreCase(Net.HttpMethods.GET)) {
                String queryString = "";
                String value = httpRequest.getContent();
                if (value != null && !"".equals(value)) queryString = "?" + value;
                url = new URL(httpRequest.getUrl() + queryString);
            } else {
                url = new URL(httpRequest.getUrl());
            }

            //Switched to https to change the SSL (Thanks Vodafone!)
            final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            // should be enabled to upload data.
            final boolean doingOutPut = method.equalsIgnoreCase(Net.HttpMethods.POST) || method.equalsIgnoreCase(Net.HttpMethods.PUT);
            connection.setDoOutput(doingOutPut);
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            HttpsURLConnection.setFollowRedirects(httpRequest.getFollowRedirects());

            putIntoConnectionsAndListeners(httpRequest, httpResponseListener, connection);

            // Headers get set regardless of the method
            for (Map.Entry<String, String> header : httpRequest.getHeaders().entrySet())
                connection.addRequestProperty(header.getKey(), header.getValue());

            // Set Timeouts
            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());

            executorService.submit(new Runnable() {
                @Override
                public void run () {
                    try {
                        // Set the content for POST and PUT (GET has the information embedded in the URL)
                        if (doingOutPut) {
                            // we probably need to use the content as stream here instead of using it as a string.
                            String contentAsString = httpRequest.getContent();
                            if (contentAsString != null) {
                                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                                try {
                                    writer.write(contentAsString);
                                } finally {
                                    StreamUtils.closeQuietly(writer);
                                }
                            } else {
                                InputStream contentAsStream = httpRequest.getContentStream();
                                if (contentAsStream != null) {
                                    OutputStream os = connection.getOutputStream();
                                    try {
                                        StreamUtils.copyStream(contentAsStream, os);
                                    } finally {
                                        StreamUtils.closeQuietly(os);
                                    }
                                }
                            }
                        }

                        connection.connect();

                        final HttpClientResponse clientResponse = new HttpClientResponse(connection);
                        try {
                            Net.HttpResponseListener listener = getFromListeners(httpRequest);

                            if (listener != null) {
                                listener.handleHttpResponse(clientResponse);
                            }
                            removeFromConnectionsAndListeners(httpRequest);
                        } finally {
                            connection.disconnect();
                        }
                    } catch (final Exception e) {
                        connection.disconnect();
                        try {
                            httpResponseListener.failed(e);
                        } finally {
                            removeFromConnectionsAndListeners(httpRequest);
                        }
                    }
                }
            });
        } catch (Exception e) {
            try {
                httpResponseListener.failed(e);
            } finally {
                removeFromConnectionsAndListeners(httpRequest);
            }
            return;
        }
    }

    public void cancelHttpRequest (Net.HttpRequest httpRequest) {
        Net.HttpResponseListener httpResponseListener = getFromListeners(httpRequest);

        if (httpResponseListener != null) {
            httpResponseListener.cancelled();
            removeFromConnectionsAndListeners(httpRequest);
        }
    }

    synchronized void removeFromConnectionsAndListeners (final Net.HttpRequest httpRequest) {
        connections.remove(httpRequest);
        listeners.remove(httpRequest);
    }

    synchronized void putIntoConnectionsAndListeners (final Net.HttpRequest httpRequest,
                                                      final Net.HttpResponseListener httpResponseListener, final HttpURLConnection connection) {
        connections.put(httpRequest, connection);
        listeners.put(httpRequest, httpResponseListener);
    }

    synchronized Net.HttpResponseListener getFromListeners (Net.HttpRequest httpRequest) {
        Net.HttpResponseListener httpResponseListener = listeners.get(httpRequest);
        return httpResponseListener;
    }
}
