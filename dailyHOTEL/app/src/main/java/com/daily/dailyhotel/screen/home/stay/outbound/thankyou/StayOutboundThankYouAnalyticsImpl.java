package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundThankYouAnalyticsImpl implements StayOutboundThankYouPresenter.StayOutboundThankYouAnalyticsInterface
{
    private StayOutboundThankYouAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayOutboundThankYouAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayOutboundThankYouAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        if (mAnalyticsParam.fullBonus == true)
        {
            params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.FULLBONUS);
        } else
        {
            switch (mAnalyticsParam.paymentType)
            {
                case EASY_CARD:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.EASYCARDPAY);
                    break;

                case CARD:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.CARDPAY);
                    break;

                case PHONE:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.PHONEBILLPAY);
                    break;
            }
        }

        params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mAnalyticsParam.registerEasyCard ? "y" : "n");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_THANKYOU_OUTBOUND, null, params);

        if (DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerEnabled() && mAnalyticsParam.provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.ORDER_COMPLETE, Integer.toString(mAnalyticsParam.stayIndex), null);
        }
    }

    @Override
    public void onEventPayment(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        String label;

        if (mAnalyticsParam.fullBonus == true)
        {
            label = AnalyticsManager.Label.FULLBONUS;
        } else if (mAnalyticsParam.usedBonus == true)
        {
            label = AnalyticsManager.Label.PAYMENTWITH_COUPON;
        } else
        {
            label = AnalyticsManager.Label.FULL_PAYMENT;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.PAYMENTUSED_OUTBOUND, label, null);
    }

    @Override
    public void onEventOrderComplete(Activity activity)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            int placeIndex = mAnalyticsParam.stayIndex;
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.ORDER_COMPLETE //
                , AnalyticsManager.ValueType.OVERSEAS, Integer.toString(placeIndex), null);
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
