package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AvailableNetwork
{
    public static final int NET_TYPE_NONE = 0x00;
    public static final int NET_TYPE_WIFI = 0x01;
    public static final int NET_TYPE_3G = 0x02;
    private static AvailableNetwork mInstance = null;

    public synchronized static AvailableNetwork getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new AvailableNetwork();
        }

        return mInstance;
    }

    private boolean getWifiState(Context context)
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (networkInfo != null && networkInfo.isConnected())
            {
                return true;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    private boolean get3GState(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (ni != null && ni.isConnected())
        {
            return true;
        }

        return false;
    }

    public int getNetType(Context context)
    {
        int nNetType = AvailableNetwork.NET_TYPE_NONE;

        if (getWifiState(context))
        {
            nNetType = AvailableNetwork.NET_TYPE_WIFI;
        } else if (get3GState(context))
        {
            nNetType = AvailableNetwork.NET_TYPE_3G;
        }

        return nNetType;
    }

    public boolean hasActiveNetwork(Context context)
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null)
        {
            ExLog.d("networkInfo : " + networkInfo);
        }

        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        } else
        {
            return false;
        }
    }
}
