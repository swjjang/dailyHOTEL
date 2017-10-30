package com.daily.dailyhotel.screen.booking.cancel.detail.stay;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayBookingCancelDetailCancelAnalyticsImpl implements StayBookingCancelDetailPresenter.StayBookingCancelAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.CANCEL_DETAIL, null);
    }

    @Override
    public StayDetailAnalyticsParam getDetailAnalyticsParam(StayBookingDetail stayBookingDetail)
    {
        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();

        return analyticsParam;
    }
}
