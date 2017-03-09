package com.twoheart.dailyhotel.deprecated;

import android.content.Context;
import android.os.PowerManager;

import com.twoheart.dailyhotel.util.ExLog;

public class WakeLock
{
    private static PowerManager.WakeLock wakeLock;

    public static void acquireWakeLock(Context context, int level)
    {
        try
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(level, context.getClass().getName());
            wakeLock.acquire();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void releaseWakeLock()
    {
        try
        {
            if (wakeLock != null)
            {
                wakeLock.release();
                wakeLock = null;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
