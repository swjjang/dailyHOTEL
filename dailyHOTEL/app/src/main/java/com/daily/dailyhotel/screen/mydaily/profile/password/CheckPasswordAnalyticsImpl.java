package com.daily.dailyhotel.screen.mydaily.profile.password;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class CheckPasswordAnalyticsImpl implements CheckPasswordPresenter.CheckPasswordAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MEMBER_LEAVE_STEP_1, null);
    }
}
