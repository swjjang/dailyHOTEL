package com.daily.dailyhotel.screen.booking.detail.map;

import android.app.Activity;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Locale;

/**
 * Created by android_sam on 2017. 7. 20..
 */

public class GourmetBookingDetailMapAnalyticsImpl implements GourmetBookingDetailMapPresenter.GourmetBookingDetailMapAnalyticsInterface
{
    @Override
    public void onItemClick(Activity activity, Gourmet gourmet, boolean isCallByThankYou)
    {
        if (activity == null)
        {
            return;
        }

        String distanceString = String.format(Locale.KOREA, "%.1f", gourmet.distance);

        String category = isCallByThankYou == true //
            ? AnalyticsManager.Category.THANKYOU_GOURMET_RECOMMEND_CLICK //
            : AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_CLICK;

        AnalyticsManager.getInstance(activity).recordEvent(//
            category, distanceString, Integer.toString(gourmet.index), null);
    }
}
