package com.daily.dailyhotel.util;

import android.content.Intent;
import android.net.Uri;

import com.daily.base.BaseActivity;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.DailyDeepLink;

public class DailyIntentUtils
{
    public static boolean hasDeepLink(Intent intent)
    {
        return intent != null || intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true;
    }

    public static DailyDeepLink getDeepLink(Intent intent)
    {
        if (hasDeepLink(intent) == false)
        {
            return null;
        }

        try
        {
            return DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK)));
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return null;
        }
    }

    public static boolean hasIntentExtras(Intent intent, String... names)
    {
        if (intent == null || names == null)
        {
            throw new NullPointerException("intent == null || names == null");
        }

        for (String name : names)
        {
            if (intent.hasExtra(name) == false)
            {
                return false;
            }
        }

        return true;
    }
}
