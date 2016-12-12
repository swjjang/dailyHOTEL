package com.twoheart.dailyhotel.network.factory;

import android.support.v4.util.ArrayMap;

import com.twoheart.dailyhotel.network.Tag;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import okhttp3.Response;
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

public class TagCancellableCallAdapterFactory extends CallAdapter.Factory
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
