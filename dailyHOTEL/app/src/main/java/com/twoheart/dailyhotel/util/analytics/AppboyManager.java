package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.appboy.Appboy;
import com.appboy.AppboyLifecycleCallbackListener;
import com.appboy.enums.NotificationSubscriptionType;
import com.appboy.models.outgoing.AppboyProperties;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class AppboyManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[AppboyManager]";

    private Appboy mAppboy;
    private String mUserIndex;

    private AppboyLifecycleCallbackListener mAppboyLifecycleCallbackListener;

    public AppboyManager(Context context)
    {
        mAppboy = Appboy.getInstance(context);
        mAppboyLifecycleCallbackListener = new AppboyLifecycleCallbackListener();
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        //        AppboyProperties appboyProperties = new AppboyProperties();
        //        appboyProperties.addProperty(screenName, AnalyticsManager.ValueType.EMPTY);
        //        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        //
        //        mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
        //
        //        if (DEBUG == true)
        //        {
        //            ExLog.d(TAG + " : " + EventName.SCREEN + ", " + appboyProperties.forJsonPut().toString());
        //        }
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        //        if (AnalyticsManager.Screen.BOOKING_LIST.equalsIgnoreCase(screenName) == true)
        //        {
        //            AppboyProperties appboyProperties = new AppboyProperties();
        //
        //            appboyProperties.addProperty(screenName, AnalyticsManager.ValueType.EMPTY);
        //            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        //
        //            try
        //            {
        //                String intValue1 = params.get(AnalyticsManager.KeyType.NUM_OF_BOOKING);
        //                appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.parseInt(intValue1));
        //
        //                mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
        //
        //                if (DEBUG == true)
        //                {
        //                    ExLog.d(TAG + " : " + EventName.SCREEN + ", " + appboyProperties.forJsonPut().toString());
        //                }
        //            } catch (NumberFormatException e)
        //            {
        //                ExLog.d(e.toString());
        //            }
        //        } else
        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty("idx", Integer.valueOf(params.get(AnalyticsManager.KeyType.PLACE_INDEX)));
            appboyProperties.addProperty("bed type_더블", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_DOUBLE)));
            appboyProperties.addProperty("bed type_트윈", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_TWIN)));
            appboyProperties.addProperty("bed type_온돌", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_IN_FLOOR_HEATING)));
            appboyProperties.addProperty("facility_부대_키즈플레이룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_KIDS_PLAY_ROOM)));
            appboyProperties.addProperty("facility_부대_수영장", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_POOL)));
            appboyProperties.addProperty("facility_부대_반려동물", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PET)));
            appboyProperties.addProperty("facility_객실내_조식", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_BREAKFAST)));
            appboyProperties.addProperty("facility_객실내_파티룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PART_ROOM)));
            appboyProperties.addProperty("facility_객실내_스파월풀", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_WHIRLPOOL)));

            mAppboy.logCustomEvent(EventName.STAY_DETAIL_CLICKED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.STAY_DETAIL_CLICKED + ", " + appboyProperties.forJsonPut().toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty("idx", params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.ADDRESS, params.get(AnalyticsManager.KeyType.ADDRESS));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWED_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.UNIT_PRICE, Integer.parseInt(params.get(AnalyticsManager.KeyType.UNIT_PRICE)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));

                mAppboy.logCustomEvent(EventName.OB_DETAIL_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.OB_DETAIL_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWED_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.UNIT_PRICE, Integer.parseInt(params.get(AnalyticsManager.KeyType.UNIT_PRICE)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, params.get(AnalyticsManager.KeyType.VISIT_DATE));

                mAppboy.logCustomEvent(EventName.GOURMET_DETAIL_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.GOURMET_DETAIL_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            firstPurchaseEventHotel(params);
        } else if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            firstPurchaseEventGourmet(params);
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (AnalyticsManager.Screen.APP_LAUNCHED.equalsIgnoreCase(category) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

            mAppboy.logCustomEvent(category, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + category + ", " + appboyProperties.forJsonPut().toString());
            }
        } else if (AnalyticsManager.Category.POPUP_BOXES.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                satisfactionCustomEvent(label);
            } else if (AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty("bed type_더블", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_DOUBLE)));
                appboyProperties.addProperty("bed type_트윈", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_TWIN)));
                appboyProperties.addProperty("bed type_온돌", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_IN_FLOOR_HEATING)));
                appboyProperties.addProperty("bed type_싱글", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_SINGLE)));
                appboyProperties.addProperty("facility_부대_키즈플레이룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_KIDS_PLAY_ROOM)));
                appboyProperties.addProperty("facility_부대_수영장", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_POOL)));
                appboyProperties.addProperty("facility_부대_반려동물", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PET)));
                appboyProperties.addProperty("facility_객실내_조식", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_BREAKFAST)));
                appboyProperties.addProperty("facility_객실내_파티룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PART_ROOM)));
                appboyProperties.addProperty("facility_객실내_스파월풀", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_WHIRLPOOL)));

                mAppboy.logCustomEvent(EventName.STAY_SORTFILTER_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.STAY_SORTFILTER_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED.equalsIgnoreCase(action) == true)
            {
                curationCustomEvent(EventName.GOURMET_SORTFILTER_CLICKED, ValueName.DAILYHOTEL, params);
            } else if (AnalyticsManager.Action.FIRST_NOTIFICATION_SETTING_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

                if (AnalyticsManager.Label.ON.equalsIgnoreCase(label) == true)
                {
                    mAppboy.logCustomEvent(EventName.FIRST_NOTIFICATION_POPUP_ON, appboyProperties);
                } else
                {
                    mAppboy.logCustomEvent(EventName.FIRST_NOTIFICATION_POPUP_OFF, appboyProperties);
                }
            }
        } else if (AnalyticsManager.Category.NAVIGATION_.equalsIgnoreCase(category) == true//
            || AnalyticsManager.Category.NAVIGATION.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.STAY_LIST_CLICK.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

                mAppboy.logCustomEvent(EventName.DAILYHOTEL_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.DAILYHOTEL_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.GOURMET_LIST_CLICK.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

                mAppboy.logCustomEvent(EventName.DAILYGOURMET_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.DAILYGOURMET_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWED_DATE, new Date());
                appboyProperties.addProperty(AnalyticsManager.KeyType.SCREEN, params.get(AnalyticsManager.KeyType.SCREEN));

                try
                {
                    appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY)));

                    mAppboy.logCustomEvent(EventName.STAY_SELECTED_DATE, appboyProperties);

                    if (DEBUG == true)
                    {
                        ExLog.d(TAG + " : " + EventName.STAY_SELECTED_DATE + ", " + appboyProperties.forJsonPut().toString());
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else if (AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWED_DATE, new Date());
                appboyProperties.addProperty(AnalyticsManager.KeyType.SCREEN, params.get(AnalyticsManager.KeyType.SCREEN));

                try
                {
                    appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, params.get(AnalyticsManager.KeyType.VISIT_DATE));

                    mAppboy.logCustomEvent(EventName.GOURMET_SELECTED_DATE, appboyProperties);

                    if (DEBUG == true)
                    {
                        ExLog.d(TAG + " : " + EventName.GOURMET_SELECTED_DATE + ", " + appboyProperties.forJsonPut().toString());
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else if (AnalyticsManager.Action.WISHLIST_ON.equalsIgnoreCase(action) == true)
            {

                String eventName;
                String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
                if (AnalyticsManager.ValueType.STAY.equalsIgnoreCase(placeType) == true)
                {
                    eventName = EventName.STAY_WISHLIST_ADDED;
                } else if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(placeType) == true)
                {
                    eventName = EventName.GOURMET_WISHLIST_ADDED;
                } else
                {
                    return;
                }

                params.remove(AnalyticsManager.KeyType.PLACE_TYPE);

                AppboyProperties appboyProperties = getAppboyProperties(params);

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                mAppboy.logCustomEvent(eventName, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + eventName + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.WISHLIST_OFF.equalsIgnoreCase(action) == true || AnalyticsManager.Action.WISHLIST_DELETE.equalsIgnoreCase(action) == true)
            {

                String eventName;
                String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
                if (AnalyticsManager.ValueType.STAY.equalsIgnoreCase(placeType) == true)
                {
                    eventName = EventName.STAY_WISHLIST_DELETED;
                } else if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(placeType) == true)
                {
                    eventName = EventName.GOURMET_WISHLIST_DELETED;
                } else
                {
                    return;
                }

                params.remove(AnalyticsManager.KeyType.PLACE_TYPE);

                AppboyProperties appboyProperties = getAppboyProperties(params);

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                mAppboy.logCustomEvent(eventName, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + eventName + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.HOME_EVENT_BANNER_CLICK.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.EVENT_IDX, label);
                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                mAppboy.logCustomEvent(EventName.HOME_BANNER_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.HOME_BANNER_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.HOME_RECOMMEND_LIST_CLICK.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.RECOMMEND_IDX, label);

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                mAppboy.logCustomEvent(EventName.HOME_RECOMMEND_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.HOME_RECOMMEND_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            }
        } else if (AnalyticsManager.Category.HOTEL_BOOKINGS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.BOOKING_CLICKED.equalsIgnoreCase(action) == true //
                || AnalyticsManager.Action.BOOKING_CLICKED_OUTBOUND.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
                appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
                appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
                appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
                appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
                appboyProperties.addProperty(AnalyticsManager.KeyType.BOOKING_INITIALISED_DATE, new Date());
                appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));

                try
                {
                    appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM)));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));

                    mAppboy.logCustomEvent(EventName.STAY_BOOKING_INITIALISED, appboyProperties);

                    if (DEBUG == true)
                    {
                        ExLog.d(TAG + " : " + EventName.STAY_BOOKING_INITIALISED + ", " + appboyProperties.forJsonPut().toString());
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_BOOKINGS.equalsIgnoreCase(category) == true//
            && AnalyticsManager.Action.BOOKING_CLICKED.equalsIgnoreCase(action) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.BOOKING_INITIALISED_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, params.get(AnalyticsManager.KeyType.VISIT_DATE));

                mAppboy.logCustomEvent(EventName.GOURMET_BOOKING_INITIALISED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.GOURMET_BOOKING_INITIALISED + ", " + appboyProperties.forJsonPut().toString());
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        } else if (AnalyticsManager.Category.COUPON_BOX.equalsIgnoreCase(category) == true//
            && AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED.equalsIgnoreCase(action) == true)
        {
            AppboyProperties appboyProperties = getAppboyProperties(params);

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

            mAppboy.logCustomEvent(EventName.COUPON_DOWNLOADED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.COUPON_DOWNLOADED + ", " + appboyProperties.forJsonPut().toString());
            }
        } else if (AnalyticsManager.Category.BOOKING_STATUS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.FREE_CANCELLATION.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = getAppboyProperties(params);

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

                mAppboy.logCustomEvent(EventName.STAY_BOOKING_CANCELED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.STAY_BOOKING_CANCELED + ", " + appboyProperties.forJsonPut().toString());
                }
            } else if (AnalyticsManager.Action.STAY_OUTBOUND_CANCELED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.STAY_NAME));
                appboyProperties.addProperty(AnalyticsManager.KeyType.CANCEL_TYPE, params.get(AnalyticsManager.KeyType.CANCEL_TYPE));

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                mAppboy.logCustomEvent(EventName.STAY_OUTBOUND_CANCELED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.STAY_OUTBOUND_CANCELED + ", " + appboyProperties.forJsonPut().toString());
                }
            }
        } else if (AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION.equalsIgnoreCase(category) == true)
        {
            if ((AnalyticsManager.Action.REVIEW_DETAIL.equalsIgnoreCase(action) == true || AnalyticsManager.Action.REVIEW_DETAIL_OB.equalsIgnoreCase(action) == true)//
                && AnalyticsManager.Label.SUBMIT.equalsIgnoreCase(label) == true)
            {
                String grade = params.get("grade");

                AppboyProperties appboyProperties = new AppboyProperties();
                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));

                if (Review.GRADE_GOOD.equalsIgnoreCase(grade) == true)
                {
                    mAppboy.logCustomEvent(EventName.STAY_SATISFACTION_DETAIL_RESPONSE, appboyProperties);
                } else if (Review.GRADE_BAD.equalsIgnoreCase(grade) == true)
                {
                    mAppboy.logCustomEvent(EventName.STAY_DISSATISFACTION_DETAIL_RESPONSE, appboyProperties);
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.REVIEW_DETAIL.equalsIgnoreCase(action) == true//
                && AnalyticsManager.Label.SUBMIT.equalsIgnoreCase(label) == true)
            {
                String grade = params.get("grade");

                AppboyProperties appboyProperties = new AppboyProperties();
                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.NAME));

                if (Review.GRADE_GOOD.equalsIgnoreCase(grade) == true)
                {
                    mAppboy.logCustomEvent(EventName.GOURMET_SATISFACTION_DETAIL_RESPONSE, appboyProperties);
                } else if (Review.GRADE_BAD.equalsIgnoreCase(grade) == true)
                {
                    mAppboy.logCustomEvent(EventName.GOURMET_DISSATISFACTION_DETAIL_RESPONSE, appboyProperties);
                }
            }
        } else if (AnalyticsManager.Category.HOME_RECOMMEND.equalsIgnoreCase(category) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.RECOMMEND_IDX, action);
            appboyProperties.addProperty(AnalyticsManager.KeyType.RECOMMEND_ITEM_IDX, label);
            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

            mAppboy.logCustomEvent(EventName.HOME_RECOMMEND_ITEM_CLICKED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.HOME_RECOMMEND_ITEM_CLICKED + ", " + appboyProperties.forJsonPut().toString());
            }
        } else if (AnalyticsManager.Action.TAG_SEARCH_NOT_FOUND.equalsIgnoreCase(action) == true || AnalyticsManager.Action.TAG_SEARCH.equalsIgnoreCase(action) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            String tagIndex = params.get(AnalyticsManager.KeyType.TAG);
            String count = params.get(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED);
            String categoryType = params.get(AnalyticsManager.KeyType.CATEGORY);

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.TAG, tagIndex);
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, count);
            appboyProperties.addProperty(AnalyticsManager.KeyType.CATEGORY, categoryType);

            mAppboy.logCustomEvent(EventName.TAG_SEARCH_TERM, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.TAG_SEARCH_TERM + ", " + appboyProperties.forJsonPut().toString());
            }
        } else if (AnalyticsManager.Category.SEARCH_.equalsIgnoreCase(category) == true//
            || AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND.equalsIgnoreCase(category) == true //
            || AnalyticsManager.Category.AUTO_SEARCH.equalsIgnoreCase(category) == true)
        {
            if (params != null)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.KEYWORD, params.get(AnalyticsManager.KeyType.SEARCH_RESULT));
                appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, params.get(AnalyticsManager.KeyType.SEARCH_COUNT));
                appboyProperties.addProperty(AnalyticsManager.KeyType.CATEGORY, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
                appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));

                mAppboy.logCustomEvent(EventName.SEARCH_TERM, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.SEARCH_TERM + ", " + appboyProperties.forJsonPut().toString());
                }
            }
        } else if (AnalyticsManager.Category.OB_SEARCH_RESULT.equalsIgnoreCase(category))
        {
            if (AnalyticsManager.Action.FILTER.equalsIgnoreCase(action))
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty("sorting", params.get("sorting"));
                appboyProperties.addProperty("country", params.get(AnalyticsManager.KeyType.COUNTRY));
                appboyProperties.addProperty("province", params.get(AnalyticsManager.KeyType.PROVINCE));

                mAppboy.logCustomEvent(EventName.OB_SORTFILTER_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.OB_SORTFILTER_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            }
        } else if (AnalyticsManager.Category.DETAILVIEW_STAY.equalsIgnoreCase(category) == true)
        {
            if ("filter_room_amenities".equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty("idx", Integer.valueOf(params.get(AnalyticsManager.KeyType.PLACE_INDEX)));
                appboyProperties.addProperty("bed type_더블", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_DOUBLE)));
                appboyProperties.addProperty("bed type_트윈", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_TWIN)));
                appboyProperties.addProperty("bed type_온돌", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.BEDTYPE_IN_FLOOR_HEATING)));
                appboyProperties.addProperty("facility_부대_키즈플레이룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_KIDS_PLAY_ROOM)));
                appboyProperties.addProperty("facility_부대_수영장", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_POOL)));
                appboyProperties.addProperty("facility_부대_반려동물", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PET)));
                appboyProperties.addProperty("facility_객실내_조식", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_BREAKFAST)));
                appboyProperties.addProperty("facility_객실내_파티룸", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_PART_ROOM)));
                appboyProperties.addProperty("facility_객실내_스파월풀", "true".equalsIgnoreCase(params.get(AnalyticsManager.KeyType.FACILITY_WHIRLPOOL)));

                mAppboy.logCustomEvent(EventName.STAY_DETAIL_CLICKED, appboyProperties);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + " : " + EventName.STAY_DETAIL_CLICKED + ", " + appboyProperties.forJsonPut().toString());
                }
            }
        }
    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

    private void searchCustomEvent(String eventName, String category, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.CATEGORY, category);
        appboyProperties.addProperty(AnalyticsManager.KeyType.KEYWORD, params.get(AnalyticsManager.KeyType.KEYWORD));

        try
        {
            int count = Integer.parseInt(params.get(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED));
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, count);

            mAppboy.logCustomEvent(eventName, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + eventName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (NumberFormatException e)
        {
            ExLog.d(e.toString());
        }
    }

    private void curationCustomEvent(String eventName, String category, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.CATEGORY, category);
        appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));

        Constants.SortType sortType = Constants.SortType.valueOf(params.get(AnalyticsManager.KeyType.SORTING));

        switch (sortType)
        {
            case DEFAULT:
                appboyProperties.addProperty(AnalyticsManager.KeyType.SORTING, ValueName.DISTRICT);
                break;

            case DISTANCE:
                appboyProperties.addProperty(AnalyticsManager.KeyType.SORTING, ValueName.DISTANCE);
                break;

            case LOW_PRICE:
                appboyProperties.addProperty(AnalyticsManager.KeyType.SORTING, ValueName.LOWTOHIGH_PRICE_SORTED);
                break;

            case HIGH_PRICE:
                appboyProperties.addProperty(AnalyticsManager.KeyType.SORTING, ValueName.HIGHTOLOW_PRICE_SORTED);
                break;

            case SATISFACTION:
                appboyProperties.addProperty(AnalyticsManager.KeyType.SORTING, ValueName.RATING_SORTED);
                break;
        }

        mAppboy.logCustomEvent(eventName, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + eventName + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    private void satisfactionCustomEvent(String label)
    {
        AppboyProperties appboyProperties = new AppboyProperties();
        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

        String eventName = EventName.STAY_SATISFACTION_SURVEY;

        if (AnalyticsManager.Label.HOTEL_SATISFACTION.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.SATISFIED);
        } else if (AnalyticsManager.Label.HOTEL_DISSATISFACTION.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.DISSATISFIED);
        } else if (AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.CLOSED);
        } else if (AnalyticsManager.Label.GOURMET_SATISFACTION.equalsIgnoreCase(label) == true)
        {
            eventName = EventName.GOURMET_SATISFACTION_SURVEY;
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.SATISFIED);
        } else if (AnalyticsManager.Label.GOURMET_DISSATISFACTION.equalsIgnoreCase(label) == true)
        {
            eventName = EventName.GOURMET_SATISFACTION_SURVEY;
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.DISSATISFIED);
        } else if (AnalyticsManager.Label.GOURMET_CLOSE_BUTTON_CLICKED.equalsIgnoreCase(label) == true)
        {
            eventName = EventName.GOURMET_SATISFACTION_SURVEY;
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.CLOSED);
        } else if (AnalyticsManager.Label.OB_SATISFACTION.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.SATISFIED);
        } else if (AnalyticsManager.Label.OB_DISSATISFACTION.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.DISSATISFIED);
        } else if (AnalyticsManager.Label.OB_CLOSE_BUTTON_CLICKED.equalsIgnoreCase(label) == true)
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.POPUP_STATUS, ValueName.CLOSED);
        }

        mAppboy.logCustomEvent(eventName, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + eventName + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    private void firstPurchaseEventHotel(Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, placeName);
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        try
        {
            boolean couponRedeem = Boolean.parseBoolean(params.get(AnalyticsManager.KeyType.COUPON_REDEEM));

            appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.COUPON_REDEEM, couponRedeem);
            appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));

            mAppboy.logCustomEvent(EventName.STAY_FIRST_PURCHASE_COMPLETED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + placeName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void firstPurchaseEventGourmet(Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, placeName);
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        try
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_HOUR, params.get(AnalyticsManager.KeyType.VISIT_HOUR));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, params.get(AnalyticsManager.KeyType.VISIT_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_TICKETS, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));

            mAppboy.logCustomEvent(EventName.GOURMET_FIRST_PURCHASE_COMPLETED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + placeName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserInformation(String index, String userType)
    {
        mUserIndex = index;
        mAppboy.changeUser(index);
    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {
        mAppboy.getCurrentUser().setCustomUserAttribute("credit_limit_over", isExceedBonus);
    }

    @Override
    void onActivityCreated(Activity activity, Bundle bundle)
    {
        mAppboyLifecycleCallbackListener.onActivityCreated(activity, bundle);
    }

    @Override
    void onActivityStarted(Activity activity)
    {
        mAppboyLifecycleCallbackListener.onActivityStarted(activity);
    }

    @Override
    void onActivityStopped(Activity activity)
    {
        mAppboyLifecycleCallbackListener.onActivityStopped(activity);
    }

    @Override
    void onActivityResumed(Activity activity)
    {
        mAppboyLifecycleCallbackListener.onActivityResumed(activity);
    }

    @Override
    void onActivityPaused(Activity activity)
    {
        mAppboyLifecycleCallbackListener.onActivityPaused(activity);
    }

    @Override
    void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {
        mAppboyLifecycleCallbackListener.onActivitySaveInstanceState(activity, bundle);
    }

    @Override
    void onActivityDestroyed(Activity activity)
    {
        mAppboyLifecycleCallbackListener.onActivityDestroyed(activity);
    }

    @Override
    void currentAppVersion(String version)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.APP_VERSION, version);

        mAppboy.logCustomEvent(EventName.CURRENT_APP_VERSION, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + EventName.CURRENT_APP_VERSION + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    @Override
    void addCreditCard(String cardType)
    {
    }

    @Override
    void updateCreditCard(String cardTypes)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());


        if (DailyTextUtils.isTextEmpty(cardTypes) == true)
        {
            cardTypes = AnalyticsManager.ValueType.EMPTY;
        }

        appboyProperties.addProperty(AnalyticsManager.KeyType.CARD_ISSUING_COMPANY, cardTypes);

        mAppboy.logCustomEvent(EventName.REGISTERED_CARD_INFO, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + EventName.REGISTERED_CARD_INFO + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    @Override
    void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, userIndex);
        appboyProperties.addProperty(AnalyticsManager.KeyType.TYPE_OF_REGISTRATION, userType);
        appboyProperties.addProperty(AnalyticsManager.KeyType.REGISTRATION_DATE, new Date());
        appboyProperties.addProperty(AnalyticsManager.KeyType.REFERRAL_CODE, AnalyticsManager.ValueType.EMPTY);

        String eventName;
        if (DailyTextUtils.isTextEmpty(callByScreen) == true)
        {
            eventName = EventName.REGISTER_COMPLETED;
        } else
        {
            if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(callByScreen) //
                || AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(callByScreen))
            {
                eventName = EventName.REGISTER_COMPLETED_BEFORE_BOOKING;
            } else
            {
                eventName = EventName.REGISTER_COMPLETED;
            }
        }

        mAppboy.logCustomEvent(eventName, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + EventName.REGISTER_COMPLETED + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    @Override
    void signUpDailyUser(String userIndex, String userType, String recommender, String callByScreen)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, userIndex);
        appboyProperties.addProperty(AnalyticsManager.KeyType.TYPE_OF_REGISTRATION, AnalyticsManager.UserType.EMAIL);
        appboyProperties.addProperty(AnalyticsManager.KeyType.REGISTRATION_DATE, new Date());

        if (DailyTextUtils.isTextEmpty(recommender) == true)
        {
            recommender = AnalyticsManager.ValueType.EMPTY;
        }

        appboyProperties.addProperty(AnalyticsManager.KeyType.REFERRAL_CODE, recommender);

        String eventName;
        if (DailyTextUtils.isTextEmpty(callByScreen) == true)
        {
            eventName = EventName.REGISTER_COMPLETED;
        } else
        {
            if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(callByScreen) //
                || AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(callByScreen))
            {
                eventName = EventName.REGISTER_COMPLETED_BEFORE_BOOKING;
            } else
            {
                eventName = EventName.REGISTER_COMPLETED;
            }
        }

        mAppboy.logCustomEvent(eventName, appboyProperties);

        if (DEBUG == true)
        {
            ExLog.d(TAG + " : " + EventName.REGISTER_COMPLETED + ", " + appboyProperties.forJsonPut().toString());
        }
    }

    @Override
    void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, placeName);
        appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        boolean couponRedeem = false;

        try
        {
            couponRedeem = Boolean.parseBoolean(params.get(AnalyticsManager.KeyType.COUPON_REDEEM));

            appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.COUPON_REDEEM, couponRedeem);

            mAppboy.logPurchase("stay-" + placeName, "KRW", new BigDecimal(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), 1, appboyProperties);
            mAppboy.logCustomEvent(EventName.STAY_PURCHASE_COMPLETED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + placeName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (couponRedeem == true)
        {
            AppboyProperties appboyProperties01 = new AppboyProperties();
            appboyProperties01.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_NAME, params.get(AnalyticsManager.KeyType.COUPON_NAME));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, params.get(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.PRICE_OFF, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE_OFF)));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.EXPIRATION_DATE, params.get(AnalyticsManager.KeyType.EXPIRATION_DATE));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_CODE, params.get(AnalyticsManager.KeyType.COUPON_CODE));

            mAppboy.logCustomEvent(EventName.STAY_COUPON_REDEEMED, appboyProperties01);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.STAY_COUPON_REDEEMED + ", " + appboyProperties01.forJsonPut().toString());
            }
        }
    }

    @Override
    void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, placeName);
        appboyProperties.addProperty(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        try
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE));

            mAppboy.logPurchase("stay-" + placeName, "KRW", new BigDecimal(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), 1, appboyProperties);
            mAppboy.logCustomEvent(EventName.STAY_PURCHASE_COMPLETED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + placeName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (Exception e)
        {

        }
    }

    @Override
    void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, placeName);
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        boolean couponRedeem = false;

        try
        {
            couponRedeem = Boolean.parseBoolean(params.get(AnalyticsManager.KeyType.COUPON_REDEEM));

            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_HOUR, params.get(AnalyticsManager.KeyType.VISIT_HOUR));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, params.get(AnalyticsManager.KeyType.VISIT_DATE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_TICKETS, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));

            mAppboy.logPurchase("gourmet-" + placeName, "KRW", new BigDecimal(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)), 1, appboyProperties);
            mAppboy.logCustomEvent(EventName.GOURMET_PURCHASE_COMPLETED, appboyProperties);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + placeName + ", " + appboyProperties.forJsonPut().toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (couponRedeem == true)
        {
            AppboyProperties appboyProperties01 = new AppboyProperties();
            appboyProperties01.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_NAME, params.get(AnalyticsManager.KeyType.COUPON_NAME));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, params.get(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.PRICE_OFF, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE_OFF)));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.EXPIRATION_DATE, params.get(AnalyticsManager.KeyType.EXPIRATION_DATE));
            appboyProperties01.addProperty(AnalyticsManager.KeyType.COUPON_CODE, params.get(AnalyticsManager.KeyType.COUPON_CODE));

            mAppboy.logCustomEvent(EventName.GOURMET_COUPON_REDEEMED, appboyProperties01);

            if (DEBUG == true)
            {
                ExLog.d(TAG + " : " + EventName.GOURMET_COUPON_REDEEMED + ", " + appboyProperties01.forJsonPut().toString());
            }
        }
    }

    @Override
    void startDeepLink(Uri deepLinkUri)
    {

    }

    @Override
    void startApplication()
    {

    }

    @Override
    void onRegionChanged(String country, String provinceName)
    {

    }

    @Override
    void setPushEnabled(boolean enabled, String pushSettingType)
    {
        mAppboy.getCurrentUser().setCustomUserAttribute("notification_status", enabled);

        if (enabled == true)
        {
            mAppboy.getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.OPTED_IN);
        } else
        {
            mAppboy.getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.UNSUBSCRIBED);
        }

        mAppboy.requestImmediateDataFlush();
    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {

    }

    private String getUserIndex()
    {
        return DailyTextUtils.isTextEmpty(mUserIndex) == true ? AnalyticsManager.ValueType.EMPTY : mUserIndex;
    }

    private AppboyProperties getAppboyProperties(Map<String, String> params)
    {
        if (params == null || params.size() == 0)
        {
            return null;
        }

        AppboyProperties appboyProperties = new AppboyProperties();

        for (Map.Entry<String, String> element : params.entrySet())
        {
            String value = DailyTextUtils.isTextEmpty(element.getValue()) == true ? AnalyticsManager.ValueType.EMPTY : element.getValue();
            appboyProperties.addProperty(element.getKey(), value);
        }

        return appboyProperties;
    }

    private static final class EventName
    {
        //        public static final String SCREEN = "screen";

        public static final String SEARCH_TERM = "search_term";
        public static final String CURRENT_APP_VERSION = "current_app_version";
        public static final String REGISTERED_CARD_INFO = "registered_card_info";
        public static final String STAY_SELECTED_DATE = "stay_selected_date";
        public static final String STAY_DETAIL_CLICKED = "stay_detail_clicked";
        public static final String OB_DETAIL_CLICKED = "ob_detail_clicked";
        public static final String OB_SORTFILTER_CLICKED = "ob_sortfilter_clicked";
        public static final String STAY_BOOKING_INITIALISED = "stay_booking_initialised";
        public static final String STAY_PURCHASE_COMPLETED = "stay_purchase_completed";

        public static final String GOURMET_SELECTED_DATE = "gourmet_selected_date";
        public static final String GOURMET_DETAIL_CLICKED = "gourmet_detail_clicked";
        public static final String GOURMET_BOOKING_INITIALISED = "gourmet_booking_initialised";
        public static final String GOURMET_PURCHASE_COMPLETED = "gourmet_purchase_completed";
        public static final String REGISTER_COMPLETED = "register_completed";
        public static final String REGISTER_COMPLETED_BEFORE_BOOKING = "register_completed_before_booking";
        public static final String STAY_SATISFACTION_SURVEY = "stay_satisfaction_survey";
        public static final String STAY_SATISFACTION_DETAIL_RESPONSE = "stay_satisfaction_detail_response";
        public static final String STAY_DISSATISFACTION_DETAIL_RESPONSE = "stay_dissatisfaction_detail_response";
        public static final String GOURMET_SATISFACTION_SURVEY = "gourmet_satisfaction_survey";
        public static final String GOURMET_SATISFACTION_DETAIL_RESPONSE = "gourmet_satisfaction_detail_response";
        public static final String GOURMET_DISSATISFACTION_DETAIL_RESPONSE = "gourmet_dissatisfaction_detail_response";

        public static final String STAY_SORTFILTER_CLICKED = "stay_sortfilter_clicked";
        public static final String GOURMET_SORTFILTER_CLICKED = "gourmet_sortfilter_clicked";

        public static final String STAY_COUPON_REDEEMED = "stay_coupon_redeemed";
        public static final String GOURMET_COUPON_REDEEMED = "gourmet_coupon_redeemed";
        public static final String COUPON_DOWNLOADED = "coupon_downloaded";

        public static final String FIRST_NOTIFICATION_POPUP_ON = "first_notification_popup_on";
        public static final String FIRST_NOTIFICATION_POPUP_OFF = "first_notification_popup_off";

        public static final String STAY_FIRST_PURCHASE_COMPLETED = "stay_first_purchase_completed";
        public static final String GOURMET_FIRST_PURCHASE_COMPLETED = "gourmet_first_purchase_completed";

        public static final String DAILYHOTEL_CLICKED = "dailyhotel_clicked";
        public static final String DAILYGOURMET_CLICKED = "dailygourmet_clicked";

        public static final String STAY_BOOKING_CANCELED = "stay_booking_canceled";

        public static final String STAY_WISHLIST_ADDED = "stay_wishlist_added";
        public static final String STAY_WISHLIST_DELETED = "stay_wishlist_deleted";
        public static final String GOURMET_WISHLIST_ADDED = "gourmet_wishlist_added";
        public static final String GOURMET_WISHLIST_DELETED = "gourmet_wishlist_deleted";

        public static final String HOME_BANNER_CLICKED = "home_banner_clicked";
        public static final String HOME_RECOMMEND_CLICKED = "home_recommend_clicked";
        public static final String HOME_RECOMMEND_ITEM_CLICKED = "home_recommend_item_clicked";
        public static final String TAG_SEARCH_TERM = "tag_search_term";
        public static final String STAY_OUTBOUND_CANCELED = "stay_outbound_canceled";
    }

    private static final class ValueName
    {
        public static final String DAILYHOTEL = "dailyhotel";
        public static final String DAILYGOURMET = "dailygourmet";
        public static final String SATISFIED = "satisfied";
        public static final String DISSATISFIED = "dissatisfied";
        public static final String CLOSED = "closed";
        public static final String DISTRICT = "district";
        public static final String DISTANCE = "distance";
        public static final String LOWTOHIGH_PRICE_SORTED = "low_to_high_price_sorted";
        public static final String HIGHTOLOW_PRICE_SORTED = "high_to_low_price_sorted";
        public static final String RATING_SORTED = "rating";
    }
}
