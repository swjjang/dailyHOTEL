package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundPaymentAnalyticsImpl implements StayOutboundPaymentPresenter.StayOutboundPaymentAnalyticsInterface
{
    private StayOutboundPaymentAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayOutboundPaymentAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayOutboundPaymentAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity, StayBookDateTime stayBookDateTime)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        if (stayBookDateTime != null)
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
        }

        params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
        params.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
        params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice ? "y" : "n");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, StayOutboundPayment.PaymentType paymentType, boolean registerEasyCard)
    {
        if (activity == null || mAnalyticsParam == null || paymentType == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        switch (paymentType)
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

        params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_PAYMENTCOMPLETE_OUTBOUND, null, params);
    }

    @Override
    public StayOutboundThankYouAnalyticsParam getThankYouAnalyticsParam(StayOutboundPayment.PaymentType paymentType, boolean registerEasyCard)
    {
        StayOutboundThankYouAnalyticsParam analyticsParam = new StayOutboundThankYouAnalyticsParam();

        analyticsParam.paymentType = paymentType;
        analyticsParam.registerEasyCard = registerEasyCard;

        return analyticsParam;
    }
}
