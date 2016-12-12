package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.factory.JSONConverterFactory;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class RetrofitHttpClient implements Constants
{
    private static final String KEY = Constants.UNENCRYPTED_URL ? "androidDAILYHOTEL_MIIEowIBAA" : "NjYkNzMkNTgkNTEkMTAkMzQkNzEkMzgkMjIkNzIkNTYkNTckMzMkNzMkNjQkNDYk$REUyREY5RTTMzOTQwNzBEQUTI4Q0MyQjIB1NLzY1IOERGRMjlDODQ5OUMPRQxMzQ5KQjUZFNjdEENTCBZg1NzE5MRTlCRkQ1NEYwOQ==$";

    private static RetrofitHttpClient mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private DailyMobileService mDailyMobileService;

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
            .build();

        mRetrofit = new Retrofit.Builder().baseUrl(DailyHotelRequest.getUrlDecoderEx(URL_DAILYHOTEL_SERVER_DEFAULT))//
            .client(mOkHttpClient)//
            .addConverterFactory(JSONConverterFactory.create())//
            .addCallAdapterFactory().build();

        mDailyMobileService = mRetrofit.create(DailyMobileService.class);
    }

    public DailyMobileService getService()
    {
        return mDailyMobileService;
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

    private class TagCancellableCallAdapterFactory extends CallAdapter.Factory
    {
        // References to the last Call made for a given tag
        private final ArrayMap<String, Call> mQueuedCalls;

        private TagCancellableCallAdapterFactory()
        {
            mQueuedCalls = new ArrayMap<>(2);
        }

        public static TagCancellableCallAdapterFactory create()
        {
            return new TagCancellableCallAdapterFactory();
        }

        @Override
        public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit)
        {
            boolean hasTagAnnotation = false;
            String value = "";
            for (Annotation annotation : annotations)
            {
                // Checks if method registers to use cancelation logic
                // Extracts the relative URI from Retrofit annotations
                if (annotation instanceof Tag)
                {
                    hasTagAnnotation = true;
                } else if (annotation instanceof DELETE)
                {
                    value = ((DELETE) annotation).value();
                } else if (annotation instanceof GET)
                {
                    value = ((GET) annotation).value();
                } else if (annotation instanceof HEAD)
                {
                    value = ((HEAD) annotation).value();
                } else if (annotation instanceof PATCH)
                {
                    value = ((PATCH) annotation).value();
                } else if (annotation instanceof POST)
                {
                    value = ((POST) annotation).value();
                } else if (annotation instanceof PUT)
                {
                    value = ((PUT) annotation).value();
                }
            }
            final boolean isTagged = hasTagAnnotation;
            final String tag = value;
            // Delegates work to default behavior, this is how the logic
            // gets injected into the rest of the Retrofit data flow
            CallAdapter<?> delegate = retrofit.nextCallAdapter(this, returnType, annotations);
            // Executor that will execute the cancelations
            final ExecutorService executor = retrofit.client().getDispatcher().getExecutorService();
            return new CallAdapter<Object>()
            {
                @Override
                public Type responseType()
                {
                    return delegate.responseType();
                }

                @Override
                public <R> Object adapt(Call<R> call)
                {
                    // Only @Tag methods will use TaggedCall
                    return delegate.adapt(isTagged ? new TaggedCall<>(call, tag, mQueuedCalls, executor) : call);
                }
            };
        }

        static final class TaggedCall<T> implements Call<T>
        {
            private final Call<T> mDelegate;
            private final String mTag;
            private final ArrayMap<String, Call> mQueuedCalls;
            private final ExecutorService mExecutor;

            TaggedCall(Call<T> delegate, String tag, ArrayMap<String, Call> queuedCalls, ExecutorService executor)
            {
                mQueuedCalls = queuedCalls;
                mTag = tag;
                mDelegate = delegate;
                mExecutor = executor;
            }

            @Override
            public Response<T> execute() throws IOException
            {
                return mDelegate.execute();
            }

            @Override
            public void enqueue(Callback<T> callback)
            {
                synchronized (mQueuedCalls)
                {
                    // Cancel enqueued call for the same tag
                    if (mQueuedCalls.containsKey(mTag))
                    {
                        final Call queuedCall = mQueuedCalls.get(mTag);
                        if (queuedCall != null)
                        {
                            // https://github.com/square/okhttp/issues/1592
                            // Call.cancel() is triggering StrictMode
                            mExecutor.execute(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    queuedCall.cancel();
                                }
                            });
                        }
                        mQueuedCalls.remove(mTag);
                    }
                    // Add call to enqueued calls
                    mQueuedCalls.put(mTag, mDelegate);
                }
                mDelegate.enqueue(callback);
            }

            @Override
            public void cancel()
            {
                mDelegate.cancel();
            }

            @SuppressWarnings("CloneDoesntCallSuperClone")
            @Override
            public Call<T> clone()
            {
                return new TaggedCall<>(mDelegate.clone(), mTag, mQueuedCalls, mExecutor);
            }
        }
    }
}
