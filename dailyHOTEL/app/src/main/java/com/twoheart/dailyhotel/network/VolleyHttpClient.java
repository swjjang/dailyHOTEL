package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

public class VolleyHttpClient implements Constants
{
    private static final String KEY = Constants.UNENCRYPTED_URL ? "androidDAILYHOTEL_MIIEowIBAA" : "ODIkNjkkMTEkODYkNDQk$NzdENjkwNTQY0NUY3QjQ5QjIyNUZCRjg0NENDOTZCOEEY5OUEzQTZGQUU3NkZFOTZDQzI4NTThBRDhBMzhGQjEhVFNw==$";

    private static VolleyHttpClient mInstance;
    private RequestQueue mRequestQueue;
    private OkHttpStack mOkHttpStack;

    public synchronized static VolleyHttpClient getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new VolleyHttpClient(context);
        }

        return mInstance;
    }

    private VolleyHttpClient(Context context)
    {
        mOkHttpStack = new OkHttpStack(context);
    }

    public void newRequestQueue(Context context)
    {
        mRequestQueue = Volley.newRequestQueue(context, mOkHttpStack);
    }

    public RequestQueue getRequestQueue()
    {
        return mRequestQueue;
    }

    public static boolean isAvailableNetwork(Context context)
    {
        boolean result = false;

        AvailableNetwork availableNetwork = AvailableNetwork.getInstance();

        switch (availableNetwork.getNetType(context))
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

    public static boolean hasActiveNetwork(Context context)
    {
        return AvailableNetwork.getInstance().hasActiveNetwork(context);
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

        public OkHttpStack(Context context)
        {
            mOkHttpClient = new OkHttpClient();
            SSLContext sslContext;

            try
            {
                TrustManager[] trustManagers = new TrustManager[]{new HttpsTrustManager()};
                //                TrustManager[] trustManagers = newTrustManager(context);

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);
            } catch (Exception e)
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

        private TrustManager[] newTrustManager(Context context)
        {
            try
            {
                KeyStore trusted = KeyStore.getInstance("BKS");
                InputStream in = context.getResources().openRawResource(R.raw.daily);

                try
                {
                    trusted.load(in, DailyHotelRequest.getUrlDecoderEx(KEY).toCharArray());
                } finally
                {
                    in.close();
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trusted);
                return trustManagerFactory.getTrustManagers();
            } catch (Exception e)
            {
                throw new AssertionError(e);
            }
        }
    }
}
