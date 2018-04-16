package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StayOutboundPaymentAnalyticsImpl implements StayOutboundPaymentPresenter.StayOutboundPaymentAnalyticsInterface
{
    private StayOutboundPaymentAnalyticsParam mAnalyticsParam;
    private HashMap<String, String> mPaymentParamMap;

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
    public void onScreen(Activity activity, StayBookDateTime stayBookDateTime, int stayIndex)
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
    public void onEventEnterVendorType(Activity activity, int stayIndex, String vendorType)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.VENDOR_SELECTION_ROOM_SELECTION//
            , vendorType, Integer.toString(stayIndex), null);
    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime//
        , String stayName, DailyBookingPaymentTypeView.PaymentType paymentType, int saleType//
        , boolean registerEasyCard, UserSimpleInformation userSimpleInformation, String aggregationId)
    {
        if (activity == null || mAnalyticsParam == null || paymentType == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            if (saleType == StayOutboundPaymentPresenter.BONUS && stayOutboundPayment.totalPrice <= userSimpleInformation.bonus)
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

            int paymentPrice = stayOutboundPayment.totalPrice - userSimpleInformation.bonus;

            // Adjust
            // Session
            AnalyticsManager.getInstance(activity).onRegionChanged("outbound", AnalyticsManager.ValueType.EMPTY);

            // event
            params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice < 0 ? 0 : paymentPrice));
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
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(stayBookDateTime.getNights()));
            params.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, mAnalyticsParam.rating);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, "n");
            params.put(AnalyticsManager.KeyType.COUPON_CODE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.USED_BOUNS, saleType == StayOutboundPaymentPresenter.BONUS ? "y" : "n");
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);

            AnalyticsManager.getInstance(activity).purchaseCompleteStayOutbound(aggregationId, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (activity == null || paymentType == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.STARTPAYMENT_OUTBOUND, getPaymentType(paymentType), null);
    }

    @Override
    public void onEventEndPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (activity == null || paymentType == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ENDPAYMENT_OUTBOUND, getPaymentType(paymentType), null);
    }

    @Override
    public void onEventVendorType(Activity activity, int stayIndex, String vendorType)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.VENDOR_SELECTION_ORDER_COMPLETION//
            , vendorType, Integer.toString(stayIndex), null);
    }

    @Override
    public StayOutboundThankYouAnalyticsParam getThankYouAnalyticsParam(DailyBookingPaymentTypeView.PaymentType paymentType//
        , boolean fullBonus, int saleType, boolean registerEasyCard, int stayIndex)
    {
        StayOutboundThankYouAnalyticsParam analyticsParam = new StayOutboundThankYouAnalyticsParam();

        analyticsParam.paymentType = paymentType;
        analyticsParam.fullBonus = fullBonus;
        analyticsParam.usedBonus = saleType == StayOutboundPaymentPresenter.BONUS;
        analyticsParam.registerEasyCard = registerEasyCard;
        analyticsParam.stayIndex = stayIndex;

        return analyticsParam;
    }

    @Override
    public void setPaymentParam(HashMap<String, String> param)
    {
        mPaymentParamMap = param;
    }

    @Override
    public void onEventCouponClick(Activity activity, boolean selected)
    {
        if (activity == null)
        {
            return;
        }

        if (selected == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                "OB_HotelUsingCouponClicked", AnalyticsManager.Label.HOTEL_USING_COUPON_CLICKED, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                "OB_HotelUsingCouponCancel", AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL_CLICKED, null);
        }
    }

    @Override
    public void onEventNotAvailableCoupon(Activity activity, String stayName, int roomPrice)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            "OB_HotelCouponNotFound", String.format(Locale.KOREA, "Hotel-%s-%d", stayName, roomPrice), null);
    }

    @Override
    public HashMap<String, String> getPaymentParam()
    {
        return mPaymentParamMap;
    }

    private String getPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return null;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                return AnalyticsManager.Label.EASYCARDPAY;

            case CARD:
                return AnalyticsManager.Label.CARDPAY;

            case PHONE:
                return AnalyticsManager.Label.PHONEBILLPAY;

            case VBANK:
                return AnalyticsManager.Label.VIRTUALACCOUNTPAY;

            case FREE:
                return AnalyticsManager.Label.FULLBONUS;
        }

        return null;
    }
}
