package com.daily.dailyhotel.screen.home.gourmet.payment;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetPayment;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GourmetPaymentAnalyticsImpl implements GourmetPaymentPresenter.GourmetPaymentAnalyticsInterface
{
    private GourmetPaymentAnalyticsParam mAnalyticsParam;
    private Map<String, String> mPaymentParamMap;

    @Override
    public void setAnalyticsParam(GourmetPaymentAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public GourmetPaymentAnalyticsParam getAnalyticsParam()
    {
        return mAnalyticsParam;
    }

    @Override
    public void onScreen(Activity activity, GourmetBookDateTime gourmetBookDateTime, int gourmetIndex//
        , String gourmetName, int menuIndex, String menuName, String category//
        , GourmetPayment gourmetPayment, boolean registerEasyCard)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.NAME, gourmetName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetPayment.totalPrice));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetIndex));
            params.put(AnalyticsManager.KeyType.DATE, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, menuName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(menuIndex));
            params.put(AnalyticsManager.KeyType.CATEGORY, category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(mAnalyticsParam.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice);
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAnalyticsProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAnalyticsDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAnalyticsAddressAreaName());

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onScreenAgreeTermDialog(Activity activity, String visitDateTime, int gourmetIndex//
        , String gourmetName, int menuIndex, String menuName, int menuCount, String category//
        , GourmetPayment gourmetPayment, boolean registerEasyCard, boolean usedBonus, boolean usedCoupon//
        , Coupon coupon, DailyBookingPaymentTypeView.PaymentType paymentType, UserSimpleInformation userSimpleInformation)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            mPaymentParamMap = new HashMap<>();

            mPaymentParamMap.put(AnalyticsManager.KeyType.NAME, gourmetName);
            mPaymentParamMap.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetPayment.totalPrice));
            mPaymentParamMap.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(menuCount));
            mPaymentParamMap.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(gourmetPayment.totalPrice * menuCount));
            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetIndex));
            mPaymentParamMap.put(AnalyticsManager.KeyType.TICKET_NAME, menuName);
            mPaymentParamMap.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(menuIndex));
            mPaymentParamMap.put(AnalyticsManager.KeyType.DATE, DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.CATEGORY, category);
            mPaymentParamMap.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
            mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_TYPE, getPaymentType(paymentType));
            mPaymentParamMap.put(AnalyticsManager.KeyType.RESERVATION_TIME, DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.VISIT_HOUR, DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.RATING, Integer.toString(mAnalyticsParam.ratingValue));
            mPaymentParamMap.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice);
            mPaymentParamMap.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            mPaymentParamMap.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(menuCount));
            mPaymentParamMap.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAnalyticsProvinceName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAnalyticsDistrictName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAnalyticsAddressAreaName());

            if(usedBonus == true)
            {

            } else if(usedCoupon == true)
            {
                int paymentPrice = gourmetPayment.totalPrice - coupon.amount;

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
                mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(gourmetPayment.totalPrice));
            }

            mPaymentParamMap.put(AnalyticsManager.KeyType.VISIT_DATE, DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP//
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

        AnalyticsManager.getInstance(activity).purchaseCompleteGourmet(transId, mPaymentParamMap);
    }

    @Override
    public void onEventChangedPrice(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, gourmetName, null);
    }

    @Override
    public void onEventSoldOut(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT, gourmetName, null);
    }

    @Override
    public void onEventBonusClick(Activity activity, boolean selected, int bonus)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
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
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                AnalyticsManager.Action.GOURMET_USING_COUPON_CLICKED, AnalyticsManager.Label.GOURMET_USING_COUPON_CLICKED, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                AnalyticsManager.Action.GOURMET_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.GOURMET_USING_COUPON_CANCEL_CLICKED, null);
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
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_BOOKING_INITIALISE, null);
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

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.THIRD_PARTY_PROVIDER_CHECK, AnalyticsManager.Label.GOURMET, null);
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
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_REGISTRATION, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
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

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.START_PAYMENT, getPaymentType(paymentType), null);
    }

    @Override
    public void onEventAgreedTermClick(Activity activity, String gourmetName, String menuName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.PAYMENT_CLICKED, String.format(Locale.KOREA, "%s-%s", gourmetName, menuName), null);
    }

    @Override
    public GourmetThankYouAnalyticsParam getThankYouAnalyticsParam()
    {
        GourmetThankYouAnalyticsParam analyticsParam = new GourmetThankYouAnalyticsParam();
        analyticsParam.params = mPaymentParamMap;

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
