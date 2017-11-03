package com.daily.dailyhotel.screen.mydaily.reward;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class RewardAnalyticsImpl implements RewardPresenter.RewardAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILY_REWARD_DETAIL, null);
    }

    @Override
    public void onEventHistoryClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
            , AnalyticsManager.Action.MYDAILY_REWARD_HISTORY, null, null);
    }

    @Override
    public void onEventTermsClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
            , AnalyticsManager.Action.MYDAILY_REWARD_INFO, null, null);
    }
}
