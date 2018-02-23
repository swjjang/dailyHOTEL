package com.daily.dailyhotel.screen.common.dialog.list;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class BaseListDialogAnalyticsImpl implements BaseListDialogInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, String screenName)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(screenName))
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, screenName, null);
    }
}
