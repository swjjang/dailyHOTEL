package com.daily.dailyhotel.screen.home.stay.inbound.payment;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
    public void onScreen(Activity activity, String refundPolicy, StayBookDateTime stayBookDateTime, int stayIndex, String stayName//
        , int roomIndex, String roomName, String category, String grade, StayPayment stayPayment, boolean registerEasyCard)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        String screenName;

        // Analytics
        if (DailyTextUtils.isTextEmpty(refundPolicy) == false)
        {
            switch (refundPolicy)
            {
                case StayRefundPolicy.STATUS_NO_CHARGE_REFUND:
                    screenName = AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE;
                    break;

                case StayRefundPolicy.STATUS_SURCHARGE_REFUND:
                    screenName = AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELLATIONFEE;
                    break;

                default:
                    screenName = AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS;
                    break;
            }
        } else
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE;
        }

        try
        {
            int nights = stayBookDateTime.getNights();
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.NAME, stayName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(mAnalyticsParam.averageDiscount));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(stayPayment.totalPrice));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, roomName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(roomIndex));
            params.put(AnalyticsManager.KeyType.GRADE, grade);
            params.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.ADDRESS, mAnalyticsParam.address);
            params.put(AnalyticsManager.KeyType.CATEGORY, category);
            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
            params.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(mAnalyticsParam.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice);
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAnalyticsProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAnalyticsDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAnalyticsAddressAreaName());

            AnalyticsManager.getInstance(activity).recordScreen(activity, screenName, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onScreenAgreeTermDialog(Activity activity, String refundPolicy, StayBookDateTime stayBookDateTime//
        , int stayIndex, String stayName, int roomIndex, String roomName, String category, String grade//
        , StayPayment stayPayment, boolean registerEasyCard)
    {

    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, StayPayment stayPayment, StayBookDateTime stayBookDateTime//
        , int stayIndex, String stayName, DailyBookingPaymentTypeView.PaymentType paymentType, boolean usedBonus, boolean registerEasyCard, UserSimpleInformation userSimpleInformation)
    {
        //        if (activity == null || mAnalyticsParam == null || paymentType == null)
        //        {
        //            return;
        //        }
        //
        //        try
        //        {
        //            Map<String, String> params = new HashMap<>();
        //
        //            if (usedBonus == true && stayPayment.totalPrice <= userSimpleInformation.bonus)
        //            {
        //                params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.FULLBONUS);
        //            } else
        //            {
        //                switch (paymentType)
        //                {
        //                    case EASY_CARD:
        //                        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.EASYCARDPAY);
        //                        break;
        //
        //                    case CARD:
        //                        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.CARDPAY);
        //                        break;
        //
        //                    case PHONE:
        //                        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, AnalyticsManager.Label.PHONEBILLPAY);
        //                        break;
        //                }
        //            }
        //
        //            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
        //
        //            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_PAYMENTCOMPLETE_OUTBOUND, null, params);
        //
        //            int paymentPrice = stayPayment.totalPrice - userSimpleInformation.bonus;
        //
        //            // Adjust
        //            // Session
        //            AnalyticsManager.getInstance(activity).onRegionChanged("outbound", AnalyticsManager.ValueType.EMPTY);
        //
        //            // event
        //            params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice < 0 ? 0 : paymentPrice));
        //            params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.GRADE, mAnalyticsParam.grade);
        //            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));
        //            params.put(AnalyticsManager.KeyType.NAME, stayName);
        //            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice ? "y" : "n");
        //            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
        //            params.put(AnalyticsManager.KeyType.DBENEFIT, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.TICKET_INDEX, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
        //            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
        //            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookDateTime.getNights()));
        //            params.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
        //            params.put(AnalyticsManager.KeyType.RATING, mAnalyticsParam.rating);
        //            params.put(AnalyticsManager.KeyType.DAILYCHOICE, "n");
        //            params.put(AnalyticsManager.KeyType.COUPON_CODE, AnalyticsManager.ValueType.EMPTY);
        //            params.put(AnalyticsManager.KeyType.USED_BOUNS, usedBonus ? "y" : "n");
        //
        //            String strDate = DailyCalendar.format(new Date(), "yyyyMMddHHmmss");
        //            String transId = strDate + '_' + userSimpleInformation.index;
        //
        //            AnalyticsManager.getInstance(activity).purchaseCompleteStayOutbound(transId, params);
        //        } catch (Exception e)
        //        {
        //            ExLog.e(e.toString());
        //        }
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
    public void onEventChangedPrice(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, stayName, null);
    }

    @Override
    public void onEventSoldOut(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT, stayName, null);
    }

    @Override
    public void onEventBonusClick(Activity activity, boolean selected, int bonus)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , selected ? AnalyticsManager.Action.USING_CREDIT_CLICKED : AnalyticsManager.Action.USING_CREDIT_CANCEL_CLICKED//
            , Integer.toString(bonus), null);
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
                AnalyticsManager.Action.HOTEL_USING_COUPON_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CLICKED, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL_CLICKED, null);
        }
    }

    @Override
    public void onEventTransportationType(Activity activity, String transportation, String type)
    {
        if (activity == null)
        {
            return;
        }

        if (StayPayment.VISIT_TYPE_PARKING.equalsIgnoreCase(transportation) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                AnalyticsManager.Action.WAYTOVISIT_SELECTED, StayPaymentPresenter.WALKING.equalsIgnoreCase(type) == true ? AnalyticsManager.Label.WALK : AnalyticsManager.Label.CAR, null);
        } else if (StayPayment.VISIT_TYPE_NO_PARKING.equalsIgnoreCase(transportation) == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                AnalyticsManager.Action.WAYTOVISIT_SELECTED, AnalyticsManager.Label.PARKING_NOT_AVAILABLE, null);
        }
    }

    @Override
    public void onEventEasyCardManagerClick(Activity activity, boolean hasEasyCard)
    {
        if (activity == null)
        {
            return;
        }

        if (hasEasyCard == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_REGISTRATION, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_EDIT, null);
        }
    }

    @Override
    public void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (activity == null || paymentType == null)
        {
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                mStartPaymentType = AnalyticsManager.Label.EASYCARDPAY;
                break;

            case CARD:
                mStartPaymentType = AnalyticsManager.Label.CARDPAY;
                break;

            case PHONE:
                mStartPaymentType = AnalyticsManager.Label.PHONEBILLPAY;
                break;

            case VBANK:
                mStartPaymentType = AnalyticsManager.Label.VIRTUALACCOUNTPAY;
                break;

            case FREE:
                mStartPaymentType = AnalyticsManager.Label.FULLBONUS;
                break;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.START_PAYMENT, mStartPaymentType, null);
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
