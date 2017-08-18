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
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StayPaymentAnalyticsImpl implements StayPaymentPresenter.StayPaymentAnalyticsInterface
{
    private StayPaymentAnalyticsParam mAnalyticsParam;
    private Map<String, String> mPaymentParamMap;

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

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));
            params.put(AnalyticsManager.KeyType.NAME, stayName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(mAnalyticsParam.averageDiscount));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(stayPayment.totalPrice));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
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
    public void onScreenAgreeTermDialog(Activity activity, StayBookDateTime stayBookDateTime//
        , int stayIndex, String stayName, int roomIndex, String roomName, String category, String grade//
        , StayPayment stayPayment, boolean registerEasyCard, boolean usedBonus, boolean usedCoupon, Coupon coupon//
        , DailyBookingPaymentTypeView.PaymentType paymentType, UserSimpleInformation userSimpleInformation)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            int nights = stayBookDateTime.getNights();
            mPaymentParamMap = new HashMap<>();

            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));
            mPaymentParamMap.put(AnalyticsManager.KeyType.NAME, stayName);
            mPaymentParamMap.put(AnalyticsManager.KeyType.PRICE, Integer.toString(mAnalyticsParam.averageDiscount));
            mPaymentParamMap.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(stayPayment.totalPrice));
            mPaymentParamMap.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            mPaymentParamMap.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
            mPaymentParamMap.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.TICKET_NAME, roomName);
            mPaymentParamMap.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(roomIndex));
            mPaymentParamMap.put(AnalyticsManager.KeyType.GRADE, grade);
            mPaymentParamMap.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
            mPaymentParamMap.put(AnalyticsManager.KeyType.ADDRESS, mAnalyticsParam.address);
            mPaymentParamMap.put(AnalyticsManager.KeyType.CATEGORY, category);
            mPaymentParamMap.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.NRD, mAnalyticsParam.nrd ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.RATING, Integer.toString(mAnalyticsParam.ratingValue));
            mPaymentParamMap.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice);
            mPaymentParamMap.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            mPaymentParamMap.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAnalyticsProvinceName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAnalyticsDistrictName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAnalyticsAddressAreaName());

            // 여기까지 onScreen과 다름.
            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(mAnalyticsParam.totalListCount));

            if (usedBonus == true)
            {
                int paymentPrice = stayPayment.totalPrice - userSimpleInformation.bonus;
                int discountPrice = paymentPrice < 0 ? stayPayment.totalPrice : userSimpleInformation.bonus;

                mPaymentParamMap.put(AnalyticsManager.KeyType.USED_BOUNS, Integer.toString(discountPrice));
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_NAME, "");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_CODE, "");
                mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice < 0 ? 0 : paymentPrice));
            } else if (usedCoupon == true)
            {
                int paymentPrice = stayPayment.totalPrice - coupon.amount;

                mPaymentParamMap.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_REDEEM, "true");
                mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice < 0 ? 0 : paymentPrice));
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                mPaymentParamMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));

                String expireDate = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm");
                mPaymentParamMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, expireDate);
            } else
            {
                mPaymentParamMap.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_NAME, "");
                mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_CODE, "");
                mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(stayPayment.totalPrice));
            }

            mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_TYPE, getPaymentType(paymentType));

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_AGREEMENT_POPUP//
                , null, mPaymentParamMap);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, String transId)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).purchaseCompleteHotel(transId, mPaymentParamMap);
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
    public void onEventCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_BOOKING_INITIALISE, null);
    }

    @Override
    public void onEventCall(Activity activity, boolean call)
    {
        if (activity == null)
        {
            return;
        }

        if (call == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(//
                AnalyticsManager.Category.CALL_BUTTON_CLICKED, //
                AnalyticsManager.Action.BOOKING_INITIALISE, AnalyticsManager.Label.CALL, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(//
                AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                AnalyticsManager.Action.BOOKING_INITIALISE, AnalyticsManager.Label.CANCEL_, null);
        }
    }

    @Override
    public void onEventAgreedThirdPartyClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.THIRD_PARTY_PROVIDER_CHECK, AnalyticsManager.Label.STAY, null);
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
    public void onEventAgreedTermCancelClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.CANCEL_, null);
    }

    @Override
    public void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (activity == null || paymentType == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.START_PAYMENT, getPaymentType(paymentType), null);
    }

    @Override
    public void onEventAgreedTermClick(Activity activity, String stayName, String roomName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.PAYMENT_CLICKED, String.format(Locale.KOREA, "%s-%s", stayName, roomName), null);
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
