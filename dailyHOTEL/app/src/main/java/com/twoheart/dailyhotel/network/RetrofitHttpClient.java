package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.factory.JSONConverterFactory;
import com.twoheart.dailyhotel.network.factory.TagCancellableCallAdapterFactory;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;

public class RetrofitHttpClient implements Constants
{
    private static final String KEY = Constants.UNENCRYPTED_URL ? "androidDAILYHOTEL_MIIEowIBAA" : "NjYkNzMkNTgkNTEkMTAkMzQkNzEkMzgkMjIkNzIkNTYkNTckMzMkNzMkNjQkNDYk$REUyREY5RTTMzOTQwNzBEQUTI4Q0MyQjIB1NLzY1IOERGRMjlDODQ5OUMPRQxMzQ5KQjUZFNjdEENTCBZg1NzE5MRTlCRkQ1NEYwOQ==$";

    private static RetrofitHttpClient mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private DailyMobileService mDailyMobileService;
    private TagCancellableCallAdapterFactory mTagCancellableCallAdapterFactory;

    public synchronized static RetrofitHttpClient getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new RetrofitHttpClient(context);
        }

        return mInstance;
    }

    private RetrofitHttpClient(Context context)
    {
        mOkHttpClient = new OkHttpClient();
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
        mOkHttpClient = mOkHttpClient.newBuilder().sslSocketFactory(sslSocketFactory, Platform.get()//
            .trustManager(sslSocketFactory))//
            .addInterceptor(new HeaderInterceptor())//
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

        mTagCancellableCallAdapterFactory = TagCancellableCallAdapterFactory.create();

        if (Constants.DEBUG == true)
        {
            String baseUrl = DailyPreference.getInstance(context).getBaseUrl();

            mRetrofit = new Retrofit.Builder().baseUrl(baseUrl)//
                .client(mOkHttpClient)//
                .addConverterFactory(JSONConverterFactory.create())//
                .addConverterFactory(LoganSquareConverterFactory.create())//
                .addCallAdapterFactory(mTagCancellableCallAdapterFactory).build();
        } else
        {
            mRetrofit = new Retrofit.Builder().baseUrl(Crypto.getUrlDecoderEx(URL_DAILYHOTEL_SERVER_DEFAULT))//
                .client(mOkHttpClient)//
                .addConverterFactory(JSONConverterFactory.create())//
                .addCallAdapterFactory(mTagCancellableCallAdapterFactory).build();
        }

        mDailyMobileService = mRetrofit.create(DailyMobileService.class);
    }

    public DailyMobileService getService()
    {
        return mDailyMobileService;
    }

    public void cancelAll(String tag)
    {
        if (mTagCancellableCallAdapterFactory == null)
        {
            return;
        }

        mTagCancellableCallAdapterFactory.cancelAll(tag);
    }

    public void cancelAll()
    {
        if (mTagCancellableCallAdapterFactory == null)
        {
            return;
        }

        mTagCancellableCallAdapterFactory.cancelAll();
    }

    private TrustManager[] newTrustManager(Context context)
    {
        try
        {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.daily);

            try
            {
                trusted.load(in, Crypto.getUrlDecoderEx(KEY).toCharArray());
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

    private class HeaderInterceptor implements Interceptor
    {
        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();

            Request.Builder builder = request.newBuilder().addHeader("Os-Type", "android")//
                .addHeader("App-Version", DailyHotel.VERSION)//
                .addHeader("User-Agent", System.getProperty("http.agent"))//
                .addHeader("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

            if (DailyHotel.isLogin() == true)
            {
                builder.addHeader("Authorization", DailyHotel.AUTHORIZATION);
            }

            return chain.proceed(builder.build());
        }
    }
}
