package com.daily.dailyhotel.screen.mydaily.reward.history;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class RewardHistoryAnalyticsImpl implements RewardHistoryPresenter.RewardHistoryAnalyticsInterface
{
    @Override
    public void onViewReservationClick(Activity activity, String aggregationId, int reservationIndex)
    {
        if (activity == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(aggregationId) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.REWARD_HISTORY_SEE_RESERVATION, Integer.toString(reservationIndex), null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.REWARD_HISTORY_SEE_RESERVATION, aggregationId, null);
        }
    }
}