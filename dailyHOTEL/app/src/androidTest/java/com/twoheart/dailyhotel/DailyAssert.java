package com.twoheart.dailyhotel;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 12. 12..
 */

public class DailyAssert
{
    private static Call<JSONObject> mCall;
    private static Response<JSONObject> mResponse;
    private static CountDownLatch mLock;

    private static final int COUNT_DOWN_DELEY_TIME = 15;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    //        private static class DailyAssertHolder
    //        {
    //            public static final DailyAssert INSTANCE = new DailyAssert();
    //        }
    //
    //        public final DailyAssert getInstance() {
    //            return DailyAssertHolder.INSTANCE;
    //        }

    public static void startLock() throws InterruptedException
    {
        mLock = new CountDownLatch(1);
        mLock.await(COUNT_DOWN_DELEY_TIME, TIME_UNIT);
    }

    public static void setData(@NonNull Call<JSONObject> call, Response<JSONObject> response)
    {
        ExLog.d("setData");

        mCall = call;
        mResponse = response;

        Assert.assertNotNull(mCall);
        Assert.assertNotNull(mCall.request());
        Assert.assertNotNull(mResponse);
    }

    public static void clearData()
    {
        ExLog.d("clearData");
        mCall = null;
        mResponse = null;
    }

    public static void assertTrue(String message, boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertTrue(message, condition);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertTrue(boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertTrue(condition);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertFalse(String message, boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertFalse(message, condition);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertFalse(boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertFalse(condition);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void fail(Throwable e)
    {
        if (Constants.DEBUG == true)
        {
            String customMessage = getNetworkMessage(mCall, mResponse, e);
            throw new AssertionError(customMessage);
        }
    }

    public static void fail(Call<JSONObject> call, Throwable e)
    {
        if (Constants.DEBUG == true)
        {
            String customMessage = getNetworkMessage(call, mResponse, e);
            throw new AssertionError(customMessage);
        }
    }

    public static void fail(String message)
    {
        if (Constants.DEBUG == true)
        {
            String customMessage = getNetworkMessage(mCall, mResponse, new RuntimeException(message));
            throw new AssertionError(customMessage);
        }
    }

    public static void fail()
    {
        if (Constants.DEBUG == true)
        {
            String customMessage = getNetworkMessage(mCall, mResponse, null);
            throw new AssertionError(customMessage);
        }
    }

    public static void assertEquals(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, String expected, String actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String expected, String actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, double expected, double actual, double delta)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual, delta);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(double expected, double actual, double delta)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual, delta);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, float expected, float actual, float delta)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual, delta);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(float expected, float actual, float delta)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual, delta);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, long expected, long actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(long expected, long actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, boolean expected, boolean actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(boolean expected, boolean actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, byte expected, byte actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(byte expected, byte actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, char expected, char actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(char expected, char actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, short expected, short actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(short expected, short actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(String message, int expected, int actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertEquals(int expected, int actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertEquals(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNotNull(Object object)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNotNull(object);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNotNull(String message, Object object)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNotNull(message, object);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNull(Object object)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNull(object);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNull(String message, Object object)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNull(message, object);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertSame(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertSame(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertSame(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNotSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNotSame(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void assertNotSame(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.assertNotSame(expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void failSame(String message)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.failSame(message);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void failNotSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.failNotSame(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static void failNotEquals(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            try
            {
                Assert.failNotEquals(message, expected, actual);
            } catch (Throwable e)
            {
                String customMessage = getNetworkMessage(mCall, mResponse, e);
                throw new AssertionError(customMessage);
            }
        }
    }

    public static String format(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = message + getNetworkMessage(mCall, mResponse, null);
            return Assert.format(message, expected, actual);
        }

        return null;
    }

    private static String bodyToString(final RequestBody request)
    {
        if (request == null)
        {
            return null;
        }

        try
        {
            Buffer buffer = new Buffer();

            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private static String getNetworkMessage(@NonNull Call<JSONObject> call, Response<JSONObject> response, Throwable e)
    {

        Assert.assertNotNull(call);
        Assert.assertNotNull(call.request());

        String url = call.request().url().toString();
        String body = bodyToString(call.request().body());


        StringBuilder builder = new StringBuilder();

        if (e != null && TextUtils.isEmpty(e.getMessage()) == false)
        {
            builder.append(e.getMessage());
        }

        builder.append("\n===================== requset start =====================");
        builder.append("\nurl : ").append(url).append("\nbody : ").append(body);
        builder.append("\n" + "===================== request end =====================");

        if (response != null)
        {
            builder.append("\n===================== body start =====================");
            builder.append("\n").append("isSuccessful : ").append(response.isSuccessful());
            builder.append("\n").append("code : ").append(response.code());

            String bodyString;
            try
            {
                bodyString = response.body().toString(1);
            } catch (JSONException e1)
            {
                bodyString = e1.getMessage();
            }

            builder.append("\n").append("body : ").append(bodyString);
            builder.append("\n").append("message : ").append(response.message());
            builder.append("\n").append("errorBody : ").append(response.errorBody());
            builder.append("\n" + "===================== body end =====================");
        }

        builder.append("\n");
        return builder.toString();
    }
}
