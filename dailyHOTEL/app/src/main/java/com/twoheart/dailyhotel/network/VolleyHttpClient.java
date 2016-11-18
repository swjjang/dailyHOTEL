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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.OkHttpURLConnection;
import okhttp3.internal.huc.OkHttpsURLConnection;
import okhttp3.internal.platform.Platform;

public class VolleyHttpClient implements Constants
{
    private static final String KEY = Constants.UNENCRYPTED_URL ? "androidDAILYHOTEL_MIIEowIBAA" : "NjYkNzMkNTgkNTEkMTAkMzQkNzEkMzgkMjIkNzIkNTYkNTckMzMkNzMkNjQkNDYk$REUyREY5RTTMzOTQwNzBEQUTI4Q0MyQjIB1NLzY1IOERGRMjlDODQ5OUMPRQxMzQ5KQjUZFNjdEENTCBZg1NzE5MRTlCRkQ1NEYwOQ==$";

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
        private OkHttpClient mOkHttpClient;

        public OkHttpStack(Context context)
        {
            SSLContext sslContext;

            try
            {
                TrustManager[] trustManagers = newTrustManager(context);

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);
            } catch (Exception e)
            {
                throw new AssertionError(); // 시스템이 TLS를 지원하지 않습니다
            }

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, Platform.get().trustManager(sslSocketFactory)).build();
        }

        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException
        {
            return open(mOkHttpClient, url);
        }

        private HttpURLConnection open(OkHttpClient client, URL url)
        {
            if (client == null || url == null)
            {
                throw new NullPointerException("client == null || url == null");
            }

            String protocol = url.getProtocol();

            if (protocol.equals("http"))
            {
                return new OkHttpURLConnection(url, mOkHttpClient, null);
            }
            if (protocol.equals("https"))
            {
                return new OkHttpsURLConnection(url, mOkHttpClient, null);
            }
            throw new IllegalArgumentException("Unexpected protocol: " + protocol);
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
