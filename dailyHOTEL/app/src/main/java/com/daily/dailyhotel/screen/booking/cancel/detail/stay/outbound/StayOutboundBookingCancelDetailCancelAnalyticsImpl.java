package com.daily.dailyhotel.screen.booking.cancel.detail.stay.outbound;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;

public class StayOutboundBookingCancelDetailCancelAnalyticsImpl implements StayOutboundBookingCancelDetailPresenter.StayOutboundBookingCancelAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, int bookingState, int placeIndex, StayOutboundBookingDetail.RefundType refundType)
    {
        if (activity == null)
        {
            return;
        }

//        HashMap<String, String> params = new HashMap();
//        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
//        params.put(AnalyticsManager.KeyType.COUNTRY, "overseas");
//        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(placeIndex));
//
//        switch (bookingState)
//        {
//            case Booking.BOOKING_STATE_WAITING_REFUND:
//                AnalyticsManager.getInstance(activity).recordScreen(activity//
//                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null, params);
//                break;
//
//            case Booking.BOOKING_STATE_BEFORE_USE:
//                if (refundType == null)
//                {
//                    AnalyticsManager.getInstance(activity).recordScreen(activity//
//                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
//                    return;
//                }
//
//                switch (refundType)
//                {
//                    case FULL:
//                        AnalyticsManager.getInstance(activity).recordScreen(activity//
//                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE, null, params);
//                        break;
//
//                    case PARTIAL:
//                        AnalyticsManager.getInstance(activity).recordScreen(activity//
//                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE, null, params);
//                        break;
//
//                    case NRD:
//                        AnalyticsManager.getInstance(activity).recordScreen(activity//
//                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null, params);
//                        break;
//
//                    case TIMEOVER:
//                        AnalyticsManager.getInstance(activity).recordScreen(activity//
//                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
//                        break;
//                }
//                break;
//
//            case Booking.BOOKING_STATE_AFTER_USE:
//                AnalyticsManager.getInstance(activity).recordScreen(activity//
//                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT, null, params);
//                break;
//
//            default:
//                AnalyticsManager.getInstance(activity).recordScreen(activity//
//                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
//                break;
//        }
    }

    @Override
    public StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutboundBookingDetail != null)
        {
            analyticsParam.index = stayOutboundBookingDetail.stayIndex;
        }

        analyticsParam.benefit = false;
        analyticsParam.grade = null;
        analyticsParam.rankingPosition = -1;
        analyticsParam.rating = null;
        analyticsParam.listSize = -1;

        return analyticsParam;
    }
}
