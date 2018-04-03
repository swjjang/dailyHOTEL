package com.daily.dailyhotel.screen.home.gourmet.thankyou;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetThankYouAnalyticsImpl implements GourmetThankYouPresenter.GourmetThankYouAnalyticsInterface
{
    private GourmetThankYouAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(GourmetThankYouAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public GourmetThankYouAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_THANKYOU, null);
    }

    @Override
    public void onEventPayment(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null || mAnalyticsParam.params == null)
        {
            return;
        }

        try
        {
            String paymentType = mAnalyticsParam.params.get(AnalyticsManager.KeyType.PAYMENT_TYPE);

            String discountType;

            // 고메는 적립금 사용 불가
            if ("true".equalsIgnoreCase(mAnalyticsParam.params.get(AnalyticsManager.KeyType.COUPON_REDEEM)) == true)
            {
                discountType = AnalyticsManager.Label.PAYMENTWITH_COUPON;
            } else
            {
                discountType = AnalyticsManager.Label.FULL_PAYMENT;
            }

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.END_PAYMENT, paymentType, null);
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.PAYMENT_USED, discountType, null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventTracking(Activity activity, UserTracking userTracking)
    {
        if (activity == null || userTracking == null || mAnalyticsParam == null || mAnalyticsParam.params == null)
        {
            return;
        }

        String paymentType = mAnalyticsParam.params.get(AnalyticsManager.KeyType.PAYMENT_TYPE);
        boolean isFirstGourmetPurchase = userTracking.countOfGourmetPaymentCompleted == 1;
        boolean isCouponUsed = false;

        if (mAnalyticsParam.params != null && mAnalyticsParam.params.containsKey(AnalyticsManager.KeyType.COUPON_REDEEM) == true)
        {
            try
            {
                isCouponUsed = Boolean.parseBoolean(mAnalyticsParam.params.get(AnalyticsManager.KeyType.COUPON_REDEEM));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        if (isFirstGourmetPurchase == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.FIRST_PURCHASE_SUCCESS, paymentType, null);
            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS, null, mAnalyticsParam.params);
        }

        if (isCouponUsed == true)
        {
            mAnalyticsParam.params.put(AnalyticsManager.KeyType.FIRST_PURCHASE, isFirstGourmetPurchase ? "y" : "n");
            mAnalyticsParam.params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            AnalyticsManager.getInstance(activity).purchaseWithCoupon(mAnalyticsParam.params);
        }
    }

    @Override
    public void onEventConfirmClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.VIEW_BOOKING_STATUS_CLICKED, null);
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
    }

    @Override
    public void onEventOrderComplete(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null || mAnalyticsParam.params == null)
        {
            return;
        }

        try
        {
            String placeIndex = mAnalyticsParam.params.get(AnalyticsManager.KeyType.PLACE_INDEX);
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.ORDER_COMPLETE //
                , AnalyticsManager.ValueType.GOURMET, placeIndex, null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventShowBenefitAlarmPopup(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NOTIFICATION, "impression", "thankyou", null);
    }

    @Override
    public void onEventShowBenefitAlarmPopupClick(Activity activity, boolean agreed)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NOTIFICATION, "button_clicks", agreed ? "now" : "later", null);
    }
}
