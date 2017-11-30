package com.daily.dailyhotel.screen.home.gourmet.payment;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetCart;
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
    private HashMap<String, String> mPaymentParamMap;

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
    public void onScreen(Activity activity, GourmetCart gourmetCart, GourmetPayment gourmetPayment, boolean registerEasyCard)
    {
        if (activity == null || mAnalyticsParam == null || gourmetCart == null || gourmetPayment == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.NAME, gourmetCart.gourmetName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetCart.getTotalPrice()));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetCart.gourmetIndex));
            params.put(AnalyticsManager.KeyType.DATE, gourmetCart.getGourmetBookDateTime().getVisitDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetCart.gourmetCategory);
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
    public void onScreenAgreeTermDialog(Activity activity, GourmetCart gourmetCart//
        , GourmetPayment gourmetPayment, boolean registerEasyCard, int saleType//
        , Coupon coupon, DailyBookingPaymentTypeView.PaymentType paymentType, UserSimpleInformation userSimpleInformation)
    {
        if (activity == null || mAnalyticsParam == null)
        {
            return;
        }

        try
        {
            mPaymentParamMap = new HashMap<>();

            mPaymentParamMap.put(AnalyticsManager.KeyType.NAME, gourmetCart.gourmetName);
            mPaymentParamMap.put(AnalyticsManager.KeyType.PRICE, "0");
            mPaymentParamMap.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(gourmetCart.getTotalCount()));
            mPaymentParamMap.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(gourmetCart.getTotalPrice()));
            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetCart.gourmetIndex));
            mPaymentParamMap.put(AnalyticsManager.KeyType.DATE, gourmetCart.getGourmetBookDateTime().getVisitDateTime("yyyy-MM-dd"));
            mPaymentParamMap.put(AnalyticsManager.KeyType.CATEGORY, gourmetCart.gourmetCategory);
            mPaymentParamMap.put(AnalyticsManager.KeyType.DBENEFIT, mAnalyticsParam.benefit ? "yes" : "no");
            mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_TYPE, getPaymentType(paymentType));
            mPaymentParamMap.put(AnalyticsManager.KeyType.RESERVATION_TIME, DailyTextUtils.formatIntegerTimeToStringTime(gourmetCart.visitTime));
            mPaymentParamMap.put(AnalyticsManager.KeyType.VISIT_HOUR, DailyTextUtils.formatIntegerTimeToStringTime(gourmetCart.visitTime));
            mPaymentParamMap.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, registerEasyCard ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.RATING, Integer.toString(mAnalyticsParam.ratingValue));
            mPaymentParamMap.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPrice);
            mPaymentParamMap.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(mAnalyticsParam.rankingPosition));
            mPaymentParamMap.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.dailyChoice ? "y" : "n");
            mPaymentParamMap.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(gourmetCart.getTotalCount()));
            mPaymentParamMap.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAnalyticsProvinceName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAnalyticsDistrictName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAnalyticsAddressAreaName());
            mPaymentParamMap.put(AnalyticsManager.KeyType.LABEL, String.format(Locale.KOREA, "%s_%s", gourmetCart.getMenuCount() == 1 ? "single" : "multi", gourmetCart.gourmetName));

            switch (saleType)
            {
                case GourmetPaymentPresenter.BONUS:
                    break;

                case GourmetPaymentPresenter.COUPON:
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
                    break;
                }

                default:
                {
                    int paymentPrice = gourmetPayment.totalPrice;

                    mPaymentParamMap.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                    mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                    mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_NAME, "");
                    mPaymentParamMap.put(AnalyticsManager.KeyType.COUPON_CODE, "");
                    mPaymentParamMap.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(paymentPrice));
                    break;
                }
            }

            mPaymentParamMap.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetCart.getGourmetBookDateTime().getVisitDateTime("yyyyMMdd"));

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP//
                , null, mPaymentParamMap);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onScreenPaymentCompleted(Activity activity, String aggregationId)
    {
        if (activity == null)
        {
            return;
        }

        mPaymentParamMap.put(AnalyticsManager.KeyType.AGGREGATION_ID, aggregationId);

        AnalyticsManager.getInstance(activity).purchaseCompleteGourmet(aggregationId, mPaymentParamMap);
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
    public void onEventAgreedTermClick(Activity activity, GourmetCart gourmetCart)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.PAYMENT_CLICKED, String.format(Locale.KOREA, "%s_%s", gourmetCart.getMenuCount() == 1 ? "single" : "multi", gourmetCart.gourmetName), null);
    }

    @Override
    public GourmetThankYouAnalyticsParam getThankYouAnalyticsParam()
    {
        GourmetThankYouAnalyticsParam analyticsParam = new GourmetThankYouAnalyticsParam();
        analyticsParam.params = mPaymentParamMap;

        return analyticsParam;
    }

    @Override
    public void setPaymentParam(HashMap<String, String> param)
    {
        mPaymentParamMap = param;
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
