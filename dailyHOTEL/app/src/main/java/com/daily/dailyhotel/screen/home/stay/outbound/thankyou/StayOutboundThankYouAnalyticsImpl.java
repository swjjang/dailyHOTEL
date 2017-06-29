package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.app.Activity;

import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
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
            params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, "fullBonus");
        } else
        {
            switch (mAnalyticsParam.paymentType)
            {
                case EASY_CARD:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, "EasyCardPay");
                    break;

                case CARD:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, "CardPay");
                    break;

                case PHONE_PAY:
                    params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, "PhoneBillPay");
                    break;
            }
        }

        params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mAnalyticsParam.registerEasyCard ? "y" : "n");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_THANKYOU_OUTBOUND, null, params);
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
            label = "fullBonusPayment";
        } else if (mAnalyticsParam.usedBonus == true)
        {
            label = "PaymentwithCredit";
        } else
        {
            label = "FullPayment";
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.PAYMENTUSED_OUTBOUND, label, null);
    }
}
