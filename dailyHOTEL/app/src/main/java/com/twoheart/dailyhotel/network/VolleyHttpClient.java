/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * VolleyHttpClient
 * <p>
 * 네트워크 이미지 처리 및 네트워크 처리 작업을 담당하는 외부 라이브러리 Vol
 * ley를 네트워크 처리 작업을 목적으로 사용하기 위해 설정하는 유틸 클래스이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

public class VolleyHttpClient implements Constants
{
    public static final String URL_DAILYHOTEL_SERVER = URL_DAILYHOTEL_SERVER_DEFAULT;
    public static final String URL_DAILYHOTEL_SESSION_SERVER = URL_DAILYHOTEL_SESSION_SERVER_DEFAULT;
    public static final int TIME_OUT = 5000;
    public static final int MAX_RETRY = 2;
    private static RequestQueue sRequestQueue;
    private static Context sContext;
    private static OkHttpStack mOkHttpStack;

    public static void init(Context context)
    {
        sContext = context;
        mOkHttpStack = new OkHttpStack();
        sRequestQueue = Volley.newRequestQueue(sContext, mOkHttpStack);
    }

    public static RequestQueue getRequestQueue()
    {
        synchronized (VolleyHttpClient.class)
        {
            if (sRequestQueue == null)
            {
                init(sContext);
            }
        }

        return sRequestQueue;
    }

    public static boolean isAvailableNetwork()
    {
        boolean result = false;

        AvailableNetwork availableNetwork = AvailableNetwork.getInstance();

        switch (availableNetwork.getNetType(sContext))
        {
            case AvailableNetwork.NET_TYPE_WIFI:
                // WIFI 연결상태
                result = true;
                break;
            case AvailableNetwork.NET_TYPE_3G:
                // 3G 혹은 LTE연결 상태
                result = true;
                break;
            case AvailableNetwork.NET_TYPE_NONE:
                result = false;
                break;
        }
        return result;
    }

    public static boolean hasActiveNetwork()
    {
        return AvailableNetwork.getInstance().hasActiveNetwork(sContext);
    }

    private static class OkHttpStack extends HurlStack
    {
        private OkUrlFactory mOkUrlFactory;
        private OkHttpClient mOkHttpClient;

        class HttpsTrustManager implements X509TrustManager
        {
            private final X509Certificate[] x509Certificate = new X509Certificate[]{};

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
            {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
            {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return x509Certificate;
            }
        }

        public OkHttpStack()
        {
            mOkHttpClient = new OkHttpClient();
            SSLContext sslContext;

            try
            {
                TrustManager[] trustManagers = new TrustManager[]{new HttpsTrustManager()};

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, new SecureRandom());
            } catch (GeneralSecurityException e)
            {
                throw new AssertionError(); // 시스템이 TLS를 지원하지 않습니다
            }

            mOkHttpClient = mOkHttpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory()).build();

            setOkUrlFactory(new OkUrlFactory(mOkHttpClient));
        }

        public void setOkUrlFactory(OkUrlFactory okUrlFactory)
        {
            if (okUrlFactory == null)
            {
                throw new NullPointerException("Client must not be null.");
            }

            mOkUrlFactory = okUrlFactory;
        }

        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException
        {
            return mOkUrlFactory.open(url);
        }
    }
}
