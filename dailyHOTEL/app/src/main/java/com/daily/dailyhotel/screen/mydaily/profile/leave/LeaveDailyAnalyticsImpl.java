package com.daily.dailyhotel.screen.mydaily.profile.leave;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class LeaveDailyAnalyticsImpl implements LeaveDailyInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MEMBER_LEAVE_STEP_3, null);
    }

    @Override
    public void onEventLeaveReasonSelected(Activity activity, String reason)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(reason))
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.MEMBER_LEAVE, "leave_reason", reason, null);
    }

    @Override
    public void onEventLeaveButtonClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.MEMBER_LEAVE, "leave_proceeded", null, null);
    }

    @Override
    public void onEventCheckLeaveDialogButtonClick(Activity activity, boolean yes)
    {
        if (activity == null)
        {
            return;
        }

        String label = yes ? AnalyticsManager.Label.YES : AnalyticsManager.Label.NO;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.MEMBER_LEAVE, "member_left_msg", label, null);
    }
}
