package com.twoheart.dailyhotel;

import android.support.annotation.NonNull;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

//        private static class DailyAssertHolder
//        {
//            public static final DailyAssert INSTANCE = new DailyAssert();
//        }
//
//        public final DailyAssert getInstance() {
//            return DailyAssertHolder.INSTANCE;
//        }

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
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertTrue(message, condition);
        }
    }

    public static void assertTrue(boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertTrue(message, condition);
        }
    }

    public static void assertFalse(String message, boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertFalse(message, condition);
        }
    }

    public static void assertFalse(boolean condition)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertFalse(message, condition);
        }
    }

    public static void fail(Throwable e)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, (e == null) ? null : e.getMessage());
            Assert.fail(message);
        }
    }

    public static void fail(Call<JSONObject> call, Throwable e)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(call, mResponse, (e == null) ? null : e.getMessage());
            Assert.fail(message);
        }
    }

    public static void fail(String message)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.fail(message);
        }
    }

    public static void fail()
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.fail(message);
        }
    }

    public static void assertEquals(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, String expected, String actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String expected, String actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, double expected, double actual, double delta)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(double expected, double actual, double delta)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(String message, float expected, float actual, float delta)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(float expected, float actual, float delta)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(String message, long expected, long actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(long expected, long actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, boolean expected, boolean actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(boolean expected, boolean actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, byte expected, byte actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(byte expected, byte actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, char expected, char actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(char expected, char actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, short expected, short actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(short expected, short actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String message, int expected, int actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(int expected, int actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertNotNull(Object object)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertNotNull(message, object);
        }
    }

    public static void assertNotNull(String message, Object object)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertNotNull(message, object);
        }
    }

    public static void assertNull(Object object)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertNull(message, object);
        }
    }

    public static void assertNull(String message, Object object)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertNull(message, object);
        }
    }

    public static void assertSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertSame(message, expected, actual);
        }
    }

    public static void assertSame(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertSame(message, expected, actual);
        }
    }

    public static void assertNotSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.assertNotSame(message, expected, actual);
        }
    }

    public static void assertNotSame(Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            String message = getNetworkMessage(mCall, mResponse, null);
            Assert.assertNotSame(message, expected, actual);
        }
    }

    public static void failSame(String message)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.failSame(message);
        }
    }

    public static void failNotSame(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.failNotSame(message, expected, actual);
        }
    }

    public static void failNotEquals(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
            Assert.failNotEquals(message, expected, actual);
        }
    }

    public static String format(String message, Object expected, Object actual)
    {
        if (Constants.DEBUG == true)
        {
            message = getNetworkMessage(mCall, mResponse, message);
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

    private static String getNetworkMessage(@NonNull Call<JSONObject> call, Response<JSONObject> response, String message)
    {

        Assert.assertNotNull(call);
        Assert.assertNotNull(call.request());

        String url = call.request().url().toString();
        String body = bodyToString(call.request().body());


        StringBuilder builder = new StringBuilder("===================== requset start =====================");
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
            } catch (JSONException e)
            {
                bodyString = e.getMessage();
            }

            builder.append("\n").append("body : ").append(bodyString);
            builder.append("\n").append("message : ").append(response.message());
            builder.append("\n").append("errorBody : ").append(response.errorBody());
            builder.append("\n" + "===================== body end =====================");
        }

        if (Util.isTextEmpty(message) == false)
        {
            builder.append("\n").append(message);
        }

        return builder.toString();
    }
}
