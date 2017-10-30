package com.daily.dailyhotel.screen.booking.cancel.detail.gourmet;

import android.app.Activity;

import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetBookingCancelDetailCancelAnalyticsImpl implements GourmetBookingCancelDetailPresenter.GourmetBookingCancelAnalyticsInterface
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
    public GourmetDetailAnalyticsParam getDetailAnalyticsParam(GourmetBookingDetail gourmetBookingDetail)
    {
        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();

        return analyticsParam;
    }
}
