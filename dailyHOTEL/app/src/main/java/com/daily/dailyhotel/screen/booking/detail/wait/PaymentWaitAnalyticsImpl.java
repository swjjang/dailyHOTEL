package com.daily.dailyhotel.screen.booking.detail.wait;

import android.app.Activity;

import com.daily.dailyhotel.entity.Booking;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class PaymentWaitAnalyticsImpl implements PaymentWaitPresenter.PaymentWaitAnalyticsInterface
{
    @Override
    public void onEventConciergeClick(Activity activity, Booking.PlaceType placeType)
    {
        if (activity == null)
        {
            return;
        }

        switch (placeType)
        {
            case STAY:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                break;
        }
    }

    @Override
    public void onEventConciergeFaqClick(Activity activity, Booking.PlaceType placeType)
    {
        if (activity == null)
        {
            return;
        }

        switch (placeType)
        {
            case STAY:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                break;
        }
    }

    @Override
    public void onEventConciergeHappyTalkClick(Activity activity, Booking.PlaceType placeType)
    {
        if (activity == null)
        {
            return;
        }

        switch (placeType)
        {
            case STAY:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                break;
        }
    }

    @Override
    public void onEventConciergeHappyTalkClick2(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);
    }

    @Override
    public void onEventConciergeCallClick(Activity activity, Booking.PlaceType placeType)
    {
        if (activity == null)
        {
            return;
        }

    }
}
