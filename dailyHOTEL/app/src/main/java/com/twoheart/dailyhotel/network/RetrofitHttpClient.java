package com.twoheart.dailyhotel.network;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.github.aurae.retrofit2.LoganSquareConverterFactory;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.factory.JSONConverterFactory;
import com.twoheart.dailyhotel.network.factory.TagCancellableCallAdapterFactory;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitHttpClient implements Constants
{
    private static RetrofitHttpClient mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private DailyMobileService mDailyMobileService;
    private TagCancellableCallAdapterFactory mTagCancellableCallAdapterFactory;

    public synchronized static RetrofitHttpClient getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new RetrofitHttpClient();
        }

        return mInstance;
    }

    private RetrofitHttpClient()
    {
        mOkHttpClient = new OkHttpClient().newBuilder()//
            .addInterceptor(new HeaderInterceptor())//
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();
    }

    public void createService(Context context)
    {
        mTagCancellableCallAdapterFactory = TagCancellableCallAdapterFactory.create();

        if (Constants.DEBUG == true)
        {
            String baseUrl = DailyPreference.getInstance(context).getBaseUrl();

            mRetrofit = new Retrofit.Builder().baseUrl(baseUrl)//
                .client(mOkHttpClient)//
                .addConverterFactory(JSONConverterFactory.create())//
                .addConverterFactory(LoganSquareConverterFactory.create())//
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//
                .addCallAdapterFactory(mTagCancellableCallAdapterFactory).build();
        } else
        {
            mRetrofit = new Retrofit.Builder().baseUrl(Crypto.getUrlDecoderEx(Setting.getServerUrl()))//
                .client(mOkHttpClient)//
                .addConverterFactory(JSONConverterFactory.create())//
                .addConverterFactory(LoganSquareConverterFactory.create())//
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//
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

    private class HeaderInterceptor implements Interceptor
    {
        HeaderInterceptor()
        {
        }

        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();

            if (Constants.DEBUG == true)
            {
                if (request.body() == null)
                {
                    ExLog.d("url : " + request.url());
                } else
                {
                    try
                    {
                        Buffer buffer = new Buffer();
                        request.body().writeTo(buffer);
                        byte[] jsonByte = new byte[(int) request.body().contentLength()];
                        buffer.read(jsonByte);
                        ExLog.d("url : " + request.url() + ", body : " + new String(jsonByte));
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }
            }

            if (request.url().host().contains("dailyhotel") == true)
            {
                Request.Builder builder = request.newBuilder().addHeader("Os-Type", "android")//
                    .addHeader("App-Version", DailyHotel.VERSION)//
                    .addHeader("User-Agent", System.getProperty("http.agent"))//
                    .addHeader("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

                if (DailyHotel.isLogin() == true)
                {
                    builder.addHeader("Authorization", DailyHotel.AUTHORIZATION);
                }

                return chain.proceed(builder.build());
            } else
            {
                return chain.proceed(request.newBuilder().build());
            }
        }
    }
}
