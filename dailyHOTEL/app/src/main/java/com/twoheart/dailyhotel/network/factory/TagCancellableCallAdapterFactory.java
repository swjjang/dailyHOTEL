package com.twoheart.dailyhotel.network.factory;

import android.support.v4.util.ArrayMap;

import com.twoheart.dailyhotel.network.TAG;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TagCancellableCallAdapterFactory extends CallAdapter.Factory
{
    // References to the last Call made for a given tag
    private final ArrayMap<String, Call> mQueuedCalls;

    private TagCancellableCallAdapterFactory()
    {
        mQueuedCalls = new ArrayMap<>();
    }

    public static TagCancellableCallAdapterFactory create()
    {
        return new TagCancellableCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit)
    {
        final CallAdapter<?> delegate = retrofit.nextCallAdapter(this, returnType, annotations);
        final Executor callbackExecutor = retrofit.callbackExecutor();

        String value = "";
        for (Annotation annotation : annotations)
        {
            // Checks if method registers to use cancelation logic
            // Extracts the relative URI from Retrofit annotations
            if (annotation instanceof TAG)
            {
                value = ((TAG) annotation).value();
            }
        }

        final String tag = value;

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
                return delegate.adapt(new ExecutorCallbackCall<>(callbackExecutor, call, tag, mQueuedCalls));
            }
        };

        //        boolean hasTagAnnotation = false;
        //        String value = "";
        //        for (Annotation annotation : annotations)
        //        {
        //            // Checks if method registers to use cancelation logic
        //            // Extracts the relative URI from Retrofit annotations
        //            if (annotation instanceof TAG)
        //            {
        //                value = ((TAG) annotation).value();
        //            }
        //        }
        //        final boolean isTagged = hasTagAnnotation;
        //        final String tag = value;
        //        // Delegates work to default behavior, this is how the logic
        //        // gets injected into the rest of the Retrofit data flow
        //        final CallAdapter<?> delegate = retrofit.nextCallAdapter(this, returnType, annotations);
        //        // Executor that will execute the cancelations
        //        final ExecutorService executor = retrofit.callbackExecutor()
        //        return new CallAdapter<Object>()
        //        {
        //            @Override
        //            public Type responseType()
        //            {
        //                return delegate.responseType();
        //            }
        //
        //            @Override
        //            public <R> Object adapt(Call<R> call)
        //            {
        //                // Only @TAG methods will use TaggedCall
        //                return delegate.adapt(isTagged ? new TaggedCall<>(call, tag, mQueuedCalls, executor) : call);
        //            }
        //        };
    }

    static final class ExecutorCallbackCall<T> implements Call<T>
    {
        private final Executor callbackExecutor;
        private final Call<T> delegate;
        private final String mTag;
        private final ArrayMap<String, Call> mQueuedCalls;

        ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate, String tag, ArrayMap<String, Call> queuedCalls)
        {
            this.callbackExecutor = callbackExecutor;
            this.delegate = delegate;
            mTag = tag;
            mQueuedCalls = queuedCalls;
        }

        @Override
        public void enqueue(final Callback<T> callback)
        {
            if (callback == null)
            {
                throw new NullPointerException("callback == null");
            }

            delegate.enqueue(new Callback<T>()
            {
                @Override
                public void onResponse(Call<T> call, final Response<T> response)
                {
                    mQueuedCalls.re
                    callbackExecutor.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (delegate.isCanceled())
                            {
                                // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                                callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
                            } else
                            {
                                callback.onResponse(ExecutorCallbackCall.this, response);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call<T> call, final Throwable t)
                {
                    callbackExecutor.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            callback.onFailure(ExecutorCallbackCall.this, t);
                        }
                    });
                }
            });
        }

        @Override
        public boolean isExecuted()
        {
            return delegate.isExecuted();
        }

        @Override
        public Response<T> execute() throws IOException
        {
            return delegate.execute();
        }

        @Override
        public void cancel()
        {
            delegate.cancel();
        }

        @Override
        public boolean isCanceled()
        {
            return delegate.isCanceled();
        }

        @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
        @Override
        public Call<T> clone()
        {
            return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
        }

        @Override
        public Request request()
        {
            return delegate.request();
        }
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
        public retrofit2.Response<T> execute() throws IOException
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
        public boolean isExecuted()
        {
            return mDelegate.isExecuted();
        }

        @Override
        public void cancel()
        {
            mDelegate.cancel();
        }

        @Override
        public boolean isCanceled()
        {
            return mDelegate.isCanceled();
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override
        public Call<T> clone()
        {
            return new TaggedCall<>(mDelegate.clone(), mTag, mQueuedCalls, mExecutor);
        }

        @Override
        public Request request()
        {
            return null;
        }
    }
}
