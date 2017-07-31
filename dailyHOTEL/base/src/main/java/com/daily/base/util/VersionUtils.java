package com.daily.base.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class VersionUtils
{
    public static boolean isOverAPI11()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isOverAPI12()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean isOverAPI14()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isOverAPI15()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    public static boolean isUnderAPI16()
    {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isOverAPI16()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isOverAPI17()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isOverAPI19()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isOverAPI21()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOverAPI22()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean isOverAPI23()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isOverAPI24()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean equalsAPI24()
    {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.N;
    }

    public static String getAppVersionCode(Context context)
    {
        String version = null;
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = Integer.toString(packageInfo.versionCode);
        } catch (NameNotFoundException e)
        {
            ExLog.d(e.toString());
        }

        return version;
    }

    public static String getAppVersionName(Context context)
    {
        String version = null;
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (NameNotFoundException e)
        {
            ExLog.d(e.toString());
        }

        return version;
    }

    public static boolean isSamsungModel()
    {
        final String SAMSUNG = "samsung";

        if (SAMSUNG.equalsIgnoreCase(Build.BRAND) == true//
            || SAMSUNG.equalsIgnoreCase(Build.MANUFACTURER) == true)
        {
            return true;
        } else
        {
            return false;
        }
    }

}