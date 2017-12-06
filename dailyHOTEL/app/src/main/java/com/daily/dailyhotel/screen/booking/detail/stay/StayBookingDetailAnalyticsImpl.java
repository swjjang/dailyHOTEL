package com.daily.dailyhotel.screen.booking.detail.stay;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookingDetail;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayBookingDetailAnalyticsImpl implements StayBookingDetailPresenter.StayBookingDetailAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, StayBookingDetail stayBookingDetail, String refundPolicy)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventPaymentState(Activity activity, int gourmetIndex, int bookingState)
    {
        if (activity == null)
        {
            return;
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
            , AnalyticsManager.Action.BOOKING_SHARE, AnalyticsManager.Label.STAY, null);
    }

    @Override
    public void onEventShareKakaoClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.STAY_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.STAY_BOOKING_SHARE, AnalyticsManager.ValueType.ETC, null);
    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventConciergeFaqClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventFrontCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_FRONT, null);
    }

    @Override
    public void onEventFrontReservationCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_RESERVATION, null);
    }

    @Override
    public void onEventHappyTalkClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventHappyTalkClick2(Activity activity, boolean isRefund)
    {
        if (activity == null)
        {
            return;
        }

        if (isRefund == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.REFUND_INQUIRY, AnalyticsManager.Label.KAKAO, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);
        }
    }

    @Override
    public void onEventConciergeCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
    }

    @Override
    public void onEventStartConciergeCall(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(//
            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, //
            AnalyticsManager.Label.CUSTOMER_CENTER_CALL, null);
    }

    @Override
    public void onEventMapClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventViewDetailClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventReviewClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventHideBookingClick(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventHideBookingSuccess(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }
    }

    @Override
    public void onEventRefundClick(Activity activity, boolean isFreeRefund)
    {
        if (activity == null)
        {
            return;
        }

        if (isFreeRefund == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.FREE_CANCELLATION_CLICKED, null, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.REFUND_INQUIRY_CLICKED, null, null);
        }
    }
}
