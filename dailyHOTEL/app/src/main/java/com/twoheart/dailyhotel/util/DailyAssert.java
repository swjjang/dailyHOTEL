package com.twoheart.dailyhotel.util;

import junit.framework.Assert;

/**
 * Created by android_sam on 2016. 12. 12..
 */

public class DailyAssert
{
    public static void assertTrue(String message, boolean condition)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertTrue(message, condition);
        }
    }

    public static void assertTrue(boolean condition)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertTrue(condition);
        }
    }

    public static void assertFalse(String message, boolean condition)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertFalse(message, condition);
        }
    }

    public static void assertFalse(boolean condition)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertFalse(condition);
        }
    }

    public static void fail(String message)
    {
        if (Constants.TESTING == true)
        {
            Assert.fail(message);
        }
    }

    public static void fail()
    {
        if (Constants.TESTING == true)
        {
            Assert.fail();
        }
    }

    public static void assertEquals(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, String expected, String actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(String expected, String actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, double expected, double actual, double delta)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(double expected, double actual, double delta)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual, delta);
        }
    }

    public static void assertEquals(String message, float expected, float actual, float delta)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual, delta);
        }
    }

    public static void assertEquals(float expected, float actual, float delta)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual, delta);
        }
    }

    public static void assertEquals(String message, long expected, long actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(long expected, long actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, boolean expected, boolean actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(boolean expected, boolean actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, byte expected, byte actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(byte expected, byte actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, char expected, char actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(char expected, char actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, short expected, short actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(short expected, short actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertEquals(String message, int expected, int actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(int expected, int actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertEquals(expected, actual);
        }
    }

    public static void assertNotNull(Object object)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNotNull(object);
        }
    }

    public static void assertNotNull(String message, Object object)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNotNull(message, object);
        }
    }

    public static void assertNull(Object object)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNull(object);
        }
    }

    public static void assertNull(String message, Object object)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNull(message, object);
        }
    }

    public static void assertSame(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertSame(message, expected, actual);
        }
    }

    public static void assertSame(Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertSame(expected, actual);
        }
    }

    public static void assertNotSame(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNotSame(message, expected, actual);
        }
    }

    public static void assertNotSame(Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.assertNotSame(expected, actual);
        }
    }

    public static void failSame(String message)
    {
        if (Constants.TESTING == true)
        {
            Assert.failSame(message);
        }
    }

    public static void failNotSame(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.failNotSame(message, expected, actual);
        }
    }

    public static void failNotEquals(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            Assert.failNotEquals(message, expected, actual);
        }
    }

    public static String format(String message, Object expected, Object actual)
    {
        if (Constants.TESTING == true)
        {
            return Assert.format(message, expected, actual);
        }

        return null;
    }
}
