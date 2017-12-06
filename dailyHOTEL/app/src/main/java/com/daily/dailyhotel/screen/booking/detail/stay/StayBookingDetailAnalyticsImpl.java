package com.daily.dailyhotel.screen.booking.detail.stay;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;

public class StayBookingDetailAnalyticsImpl implements StayBookingDetailPresenter.StayBookingDetailAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, StayBookingDetail stayBookingDetail, String refundPolicy, int bookingState)
    {
        if (activity == null || stayBookingDetail == null)
        {
            return;
        }

        HashMap<String, String> params = new HashMap();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, stayBookingDetail.overseas == false ? AnalyticsManager.ValueType.DOMESTIC : AnalyticsManager.ValueType.OVERSEAS);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayBookingDetail.stayIndex));

        if (stayBookingDetail.readyForRefund == true)
        {
            AnalyticsManager.getInstance(activity).recordScreen(activity//
                , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null);
        } else
        {
            switch (bookingState)
            {
                case Booking.BOOKING_STATE_WAITING_REFUND:
                    AnalyticsManager.getInstance(activity).recordScreen(activity//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null);
                    break;

                case Booking.BOOKING_STATE_BEFORE_USE:
                    if (DailyTextUtils.isTextEmpty(refundPolicy) == false)
                    {
                        switch (refundPolicy)
                        {
                            case com.twoheart.dailyhotel.model.StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                                AnalyticsManager.getInstance(activity).recordScreen(activity//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE, null, params);
                                break;

                            case com.twoheart.dailyhotel.model.StayBookingDetail.STATUS_SURCHARGE_REFUND:
                                AnalyticsManager.getInstance(activity).recordScreen(activity//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE, null, params);
                                break;

                            case com.twoheart.dailyhotel.model.StayBookingDetail.STATUS_NRD:
                                AnalyticsManager.getInstance(activity).recordScreen(activity//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null, params);
                                break;

                            default:
                                AnalyticsManager.getInstance(activity).recordScreen(activity//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                                break;
                        }
                    } else
                    {
                        AnalyticsManager.getInstance(activity).recordScreen(activity//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                    }
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

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
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
    public void onEventConciergeCallClick(Activity activity, boolean isRefund)
    {
        if (activity == null)
        {
            return;
        }

        if (isRefund == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.REFUND_INQUIRY, AnalyticsManager.Label.CALL, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
        }
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
    public void onEventHideBookingClick(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.BOOKING_HISTORY_DELETE_TRY, "stay_" + stayIndex, null);
    }

    @Override
    public void onEventHideBookingSuccess(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.BOOKING_HISTORY_DELETE, "stay_" + stayIndex, null);
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

    @Override
    public void onEventRecommendGourmetList(Activity activity, boolean hasData)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_DETAIL//
            , AnalyticsManager.Action.GOURMET_RECOMMEND, hasData ? AnalyticsManager.Label.Y : AnalyticsManager.Label.N, null);
    }

    @Override
    public void onEventRecommendGourmetViewAllClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent( //
            AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_LIST_CLICK //
            , AnalyticsManager.Action.LIST_CLICK, null, null);
    }

    @Override
    public void onEventRecommendGourmetItemClick(Activity activity, int stayIndex, double distance)
    {
        if (activity == null)
        {
            return;
        }

        String distanceString = String.format(Locale.KOREA, "%.1f", distance);

        AnalyticsManager.getInstance(activity).recordEvent(//
            AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_CLICK, distanceString//
            , Integer.toString(stayIndex), null);
    }
}
