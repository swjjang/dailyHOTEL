package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserInformation;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.daily.dailyhotel.view.DailyPaymentTypeView;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StayOutboundPaymentAnalyticsImpl implements StayOutboundPaymentPresenter.StayOutboundPaymentAnalyticsInterface
{
    private StayOutboundPaymentAnalyticsParam mAnalyticsParam;
    private String mStartPaymentType;

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

        if (mAnalyticsParam.nrd == true)
        {
            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS_OUTBOUND, null, params);
        } else
        {
            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE_OUTBOUND, null, params);
        }
    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime//
        , String stayName, DailyPaymentTypeView.PaymentType paymentType, boolean fullBonus, boolean registerEasyCard, UserInformation userInformation)
    {
        if (activity == null || mAnalyticsParam == null || paymentType == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            if (fullBonus == true)
            {
                params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.FULLBONUS);
            } else
            {
                switch (paymentType)
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

            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_PAYMENTCOMPLETE_OUTBOUND, null, params);


            // Adjust
            // Session
            AnalyticsManager.getInstance(activity).onRegionChanged("outbound", AnalyticsManager.ValueType.EMPTY);

            // event
            params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(stayOutboundPayment.discountPrice));
            params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayOutboundPayment.stayIndex));
            params.put(AnalyticsManager.KeyType.NAME, stayName);
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            params.put(AnalyticsManager.KeyType.DBENEFIT, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookDateTime.getNights()));
            params.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, mAnalyticsParam.rating);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, "n");
            params.put(AnalyticsManager.KeyType.COUPON_CODE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.USED_BOUNS, stayOutboundPayment.totalPrice != stayOutboundPayment.discountPrice ? "y" : "n");

            String strDate = DailyCalendar.format(new Date(), "yyyyMMddHHmmss");
            String transId = strDate + '_' + userInformation.index;

            AnalyticsManager.getInstance(activity).purchaseCompleteStayOutbound(transId, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventStartPayment(Activity activity, String label)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(label) == true)
        {
            return;
        }

        mStartPaymentType = label;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.STARTPAYMENT_OUTBOUND, label, null);
    }

    @Override
    public void onEventEndPayment(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ENDPAYMENT_OUTBOUND, mStartPaymentType, null);
    }

    @Override
    public StayOutboundThankYouAnalyticsParam getThankYouAnalyticsParam(DailyPaymentTypeView.PaymentType paymentType//
        , boolean fullBonus, boolean usedBonus, boolean registerEasyCard)
    {
        StayOutboundThankYouAnalyticsParam analyticsParam = new StayOutboundThankYouAnalyticsParam();

        analyticsParam.paymentType = paymentType;
        analyticsParam.fullBonus = fullBonus;
        analyticsParam.usedBonus = usedBonus;
        analyticsParam.registerEasyCard = registerEasyCard;

        return analyticsParam;
    }
}
