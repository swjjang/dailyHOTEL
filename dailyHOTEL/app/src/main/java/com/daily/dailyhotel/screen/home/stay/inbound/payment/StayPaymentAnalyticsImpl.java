package com.daily.dailyhotel.screen.home.stay.inbound.payment;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StayPaymentAnalyticsImpl implements StayPaymentPresenter.StayPaymentAnalyticsInterface
{
    private StayPaymentAnalyticsParam mAnalyticsParam;
    private String mStartPaymentType;

    @Override
    public void setAnalyticsParam(StayPaymentAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public StayPaymentAnalyticsParam getAnalyticsParam()
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
    public void onScreenPaymentCompleted(Activity activity, StayPayment stayPayment, StayBookDateTime stayBookDateTime//
        , int stayIndex, String stayName, DailyBookingPaymentTypeView.PaymentType paymentType, boolean usedBonus
        , boolean registerEasyCard, UserSimpleInformation userSimpleInformation)
    {
        if (activity == null || mAnalyticsParam == null || paymentType == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            if (usedBonus == true && stayPayment.totalPrice <= userSimpleInformation.bonus)
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

            int paymentPrice = stayPayment.totalPrice - userSimpleInformation.bonus;

            // Adjust
            // Session
            AnalyticsManager.getInstance(activity).onRegionChanged("outbound", AnalyticsManager.ValueType.EMPTY);

            // event
            params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice < 0 ? 0 : paymentPrice));
            params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));
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
            params.put(AnalyticsManager.KeyType.USED_BOUNS, usedBonus ? "y" : "n");

            String strDate = DailyCalendar.format(new Date(), "yyyyMMddHHmmss");
            String transId = strDate + '_' + userSimpleInformation.index;

            AnalyticsManager.getInstance(activity).purchaseCompleteStayOutbound(transId, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventTransportationVisible(Activity activity, boolean visible)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING//
            , visible ? AnalyticsManager.Action.WAYTOVISIT_OPEN : AnalyticsManager.Action.WAYTOVISIT_CLOSE//
            , AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventCouponClick(Activity activity, boolean enable)
    {
        if(enable == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_USING_COUPON_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CLICKED, null);
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
    public StayThankYouAnalyticsParam getThankYouAnalyticsParam(DailyBookingPaymentTypeView.PaymentType paymentType//
        , boolean fullBonus, boolean usedBonus, boolean registerEasyCard)
    {
        StayThankYouAnalyticsParam analyticsParam = new StayThankYouAnalyticsParam();

        //        analyticsParam.paymentType = paymentType;
        //        analyticsParam.fullBonus = fullBonus;
        //        analyticsParam.usedBonus = usedBonus;
        //        analyticsParam.registerEasyCard = registerEasyCard;

        return analyticsParam;
    }
}
