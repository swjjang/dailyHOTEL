/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * VolleyHttpClient
 * <p/>
 * 네트워크 이미지 처리 및 네트워크 처리 작업을 담당하는 외부 라이브러리 Vol
 * ley를 네트워크 처리 작업을 목적으로 사용하기 위해 설정하는 유틸 클래스이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

import java.io.IOException;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleyHttpClient implements Constants
{
    public static final int TIME_OUT = 5000;
    public static final int MAX_RETRY = 2;
    private static final String KEY_DAILYHOTEL_COOKIE = "JSESSIONID";
    private static RequestQueue sRequestQueue;
    private static Context sContext;
    private static OkHttpStack mOkHttpStack;

    private static CookieSyncManager mCookieSyncManager;

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

    public static Boolean isAvailableNetwork()
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

    /**
     * 서버 response로부터 cookie를 가져와 기억함. 로그인 요청 후 성공적으로 응답을 받았을 경우 반드시 이 메서드를 사용해야
     * 함.
     */
    public static void createCookie()
    {
        cookieManagerCreate();

        List<HttpCookie> cookies = mOkHttpStack.getCookieStore().getCookies();

        if (cookies != null)
        {
            for (int i = 0; i < cookies.size(); i++)
            {
                HttpCookie newCookie = cookies.get(i);

                if (newCookie.getName().equals(KEY_DAILYHOTEL_COOKIE))
                {
                    StringBuilder cookieString = new StringBuilder();
                    cookieString.append(newCookie.getName()).append("=").append(newCookie.getValue());
                    CookieManager.getInstance().setAcceptCookie(true);

                    CookieManager.getInstance().setCookie(newCookie.getDomain(), cookieString.toString());

                    cookieManagerSync();
                }
            }
        }
    }

    public synchronized static void cookieManagerCreate()
    {
        if (mCookieSyncManager == null)
        {
            mCookieSyncManager = CookieSyncManager.createInstance(sContext);
        }
    }

    public static void cookieManagerSync()
    {
        cookieManagerCreate();

        mCookieSyncManager.sync();
    }

    public static void cookieManagerStopSync()
    {
        cookieManagerCreate();

        mCookieSyncManager.stopSync();
    }

    public static void cookieManagerStartSync()
    {
        cookieManagerCreate();

        mCookieSyncManager.startSync();
    }

    // 로그아웃 시 반드시 이 메서드를 사용해야 함.
    public static void destroyCookie()
    {
        cookieManagerCreate();

        CookieManager.getInstance().removeAllCookie();
        cookieManagerSync();
    }

    private static class OkHttpStack extends HurlStack
    {
        private OkUrlFactory mOkUrlFactory;
        private OkHttpClient mOkHttpClient;
        private CookieStore mCookieStore;

        class HttpsTrustManager implements X509TrustManager
        {
            private TrustManager[] trustManagers;
            private final X509Certificate[] x509Certificate = new X509Certificate[]{};

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
            {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
            {
            }

            public boolean isClientTrusted(X509Certificate[] chain)
            {
                return true;
            }

            public boolean isServerTrusted(X509Certificate[] chain)
            {
                return true;
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
                sslContext.init(null, trustManagers, null);
            } catch (GeneralSecurityException e)
            {
                throw new AssertionError(); // 시스템이 TLS를 지원하지 않습니다
            }

            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());

            java.net.CookieManager cookieManager = new java.net.CookieManager(new PersistentCookieStore(sContext.getApplicationContext()), CookiePolicy.ACCEPT_ALL);

            mCookieStore = cookieManager.getCookieStore();
            mOkHttpClient.setCookieHandler(cookieManager);

            setOkUrlFactory(new OkUrlFactory(mOkHttpClient));
        }

        public OkHttpClient getOkHttpClient()
        {
            return mOkHttpClient;
        }

        public CookieStore getCookieStore()
        {
            return mCookieStore;
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
