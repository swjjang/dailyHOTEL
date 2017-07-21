package com.daily.dailyhotel.screen.booking.detail.map;

import android.app.Activity;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 7. 20..
 */

public class GourmetBookingDetailMapAnalyticsImpl implements GourmetBookingDetailMapPresenter.GourmetBookingDetailMapAnalyticsInterface
{
    @Override
    public void onItemClick(Activity activity, Gourmet gourmet)
    {
        if (activity == null)
        {
            return;
        }

        String distanceString = String.format("%.1f", gourmet.distance);

        AnalyticsManager.getInstance(activity).recordEvent(//
            AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_CLICK, distanceString//
            , Integer.toString(gourmet.index), null);
    }
}
