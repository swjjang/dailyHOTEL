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

    @Override
    public void onEventShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.BOOKING_SHARE, AnalyticsManager.Label.GOURMET, null);
    }

    @Override
    public void onEventShareKakaoClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.ETC, null);
    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventConciergeFaqClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventRestaurantCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECT_CALL, null);
    }

    @Override
    public void onEventHappyTalkClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventHappyTalkClick2(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);
    }

    @Override
    public void onEventConciergeCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventStartConciergeCall(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(//
            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL,//
            AnalyticsManager.Label.CUSTOMER_CENTER_CALL, null);
    }

    @Override
    public void onEventMapClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.MAP_CLICK, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventViewDetailClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.BOOKING_ITEM_DETAIL_CLICK, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventReviewClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.WRITE_REVIEW, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventHideBookingClick(Activity activity, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.BOOKING_HISTORY_DELETE_TRY, "gourmet_" + gourmetIndex, null);
    }

    @Override
    public void onEventHideBookingSuccess(Activity activity, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.BOOKING_HISTORY_DELETE, "gourmet_" + gourmetIndex, null);
    }
}
