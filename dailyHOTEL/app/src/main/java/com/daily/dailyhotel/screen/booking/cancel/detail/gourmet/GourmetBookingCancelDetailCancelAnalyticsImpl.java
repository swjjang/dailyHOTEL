package com.daily.dailyhotel.screen.booking.cancel.detail.gourmet;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;

public class GourmetBookingCancelDetailCancelAnalyticsImpl implements GourmetBookingCancelDetailPresenter.GourmetBookingCancelAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, int placeIndex)
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
    public StayDetailAnalyticsParam getDetailAnalyticsParam(StayBookingDetail stayBookingDetail)
    {
        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();

        return analyticsParam;
    }
}
