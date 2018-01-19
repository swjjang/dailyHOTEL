package com.daily.dailyhotel.screen.home.stay.outbound.preview;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundPreviewAnalyticsImpl implements StayOutboundPreviewPresenter.StayOutboundPreviewAnalyticsInterface
{
    @Override
    public void onEventWishClick(Activity activity, int stayIndex, boolean isWish)
    {
        if (activity == null)
        {
            return;
        }

        String action = isWish ? AnalyticsManager.Action.WISHLIST_ON_PREVIEW : AnalyticsManager.Action.WISHLIST_OFF_PREVIEW;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , action, Integer.toString(stayIndex), null);
    }
}
