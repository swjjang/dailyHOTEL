package com.daily.dailyhotel.screen.booking.detail.gourmet;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Booking;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;

public class GourmetBookingDetailAnalyticsImpl implements GourmetBookingDetailPresenter.GourmetBookingDetailAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {

    }

    @Override
    public void onEventPaymentState(Activity activity, int gourmetIndex, int bookingState)
    {
        if (activity == null)
        {
            return;
        }

        try
        {
            HashMap<String, String> params = new HashMap();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, "gourmet");
            params.put(AnalyticsManager.KeyType.COUNTRY, "domestic");
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetIndex));

            switch (bookingState)
            {
                case Booking.BOOKING_STATE_WAITING_REFUND:
                    AnalyticsManager.getInstance(activity).recordScreen(activity//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null, params);
                    break;

                case Booking.BOOKING_STATE_BEFORE_USE:
                    AnalyticsManager.getInstance(activity).recordScreen(activity//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                    break;

                case Booking.BOOKING_STATE_AFTER_USE:
                    AnalyticsManager.getInstance(activity).recordScreen(activity//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT, null, params);
                    break;

                default:
                    AnalyticsManager.getInstance(activity).recordScreen(activity//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                    break;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
