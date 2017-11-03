package com.daily.dailyhotel.screen.mydaily.reward.history;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class RewardHistoryAnalyticsImpl implements RewardHistoryPresenter.RewardHistoryAnalyticsInterface
{
    @Override
    public void onViewReservationClick(Activity activity, String aggregationId)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
            , AnalyticsManager.Action.REWARD_HISTORY_SEE_RESERVATION, aggregationId, null);
    }
}