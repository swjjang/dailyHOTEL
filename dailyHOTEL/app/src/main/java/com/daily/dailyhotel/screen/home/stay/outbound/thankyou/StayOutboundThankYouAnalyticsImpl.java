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
        if(activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

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

        params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mAnalyticsParam.registerEasyCard ? "y" : "n");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
    }
}
