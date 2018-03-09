package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;

import java.util.Locale;
import java.util.Map;

public class GoogleAnalyticsManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[GoogleAnalyticsManager]";
    private static final String GA_PROPERTY_ID = DEBUG ? "UA-80273653-1" : "UA-43721645-6";

    private Tracker mGoogleAnalyticsTracker;
    private String mClientId;

    interface OnClientIdListener
    {
        void onResponseClientId(String clientId);
    }

    public GoogleAnalyticsManager(Context context, final OnClientIdListener listener)
    {
        final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setLocalDispatchPeriod(60);

        mGoogleAnalyticsTracker = googleAnalytics.newTracker(GA_PROPERTY_ID);
        mGoogleAnalyticsTracker.enableAdvertisingIdCollection(true);
        mGoogleAnalyticsTracker.set("&cu", "KRW");

        mClientId = mGoogleAnalyticsTracker.get("&cid");

        if (listener != null)
        {
            listener.onResponseClientId(mClientId);
        }
    }

    public String getClientId()
    {
        return mClientId;
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        if (AnalyticsManager.Screen.MENU_REGISTRATION_CONFIRM.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.MENU_LOGIN_COMPLETE.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
            screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else if (AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
            screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else if (AnalyticsManager.Screen.HOME_EVENT_DETAIL.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.RECOMMEND_LIST.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            if (DailyHotel.isLogin() == true)
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);
            } else
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);
            }

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else if (AnalyticsManager.Screen.MYDAILY.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.MENU.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            if (DailyHotel.isLogin() == true)
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);
            } else
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);
            }

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else
        {
            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Screen : " + screenName);
        }
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        if (params == null || DailyTextUtils.isTextEmpty(screenName) == true)
        {
            return;
        }

        if (AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            String checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);

            if (DailyTextUtils.isTextEmpty(checkIn) == false)
            {
                screenViewBuilder.setCustomDimension(1, checkIn);
            }

            if (DailyTextUtils.isTextEmpty(checkOut) == false)
            {
                screenViewBuilder.setCustomDimension(2, checkOut);
            }

            screenViewBuilder.setCustomDimension(5, params.get(AnalyticsManager.KeyType.IS_SIGNED));
            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(19, params.get(AnalyticsManager.KeyType.PLACE_HIT_TYPE));
            screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
            screenViewBuilder.setCustomDimension(8, params.get(AnalyticsManager.KeyType.PROVINCE));

            String district = params.get(AnalyticsManager.KeyType.DISTRICT);
            if (DailyTextUtils.isTextEmpty(district) == false)
            {
                screenViewBuilder.setCustomDimension(12, district);
            }

            String category = params.get(AnalyticsManager.KeyType.CATEGORY);
            if (DailyTextUtils.isTextEmpty(category) == false)
            {
                screenViewBuilder.setCustomDimension(13, category);
            }

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }

        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            checkoutStep(1, screenName, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL_ROOMTYPE.equalsIgnoreCase(screenName) == true)
        {
            checkoutStep(2, screenName, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELLATIONFEE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true)
        {
            checkoutStep(3, screenName, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_AGREEMENT_POPUP.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP.equalsIgnoreCase(screenName) == true)
        {
            checkoutStep(4, screenName, null, params);
        } else if (AnalyticsManager.Screen.BOOKING_LIST.equalsIgnoreCase(screenName) == true)
        {
            recordScreen(activity, screenName, screenClassOverride);
        } else if (AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(screenName) == true //
            || screenName.startsWith(AnalyticsManager.Screen.SEARCH_RESULT) == true //
            || screenName.startsWith(AnalyticsManager.Screen.SEARCH_RESULT_EMPTY) == true)
        {
            recordSearchAnalytics(screenName, params);
        } else if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            recordScreen(activity, screenName, screenClassOverride);
        } else if (AnalyticsManager.Screen.MENU_RECENT_VIEW.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.MENU_WISHLIST.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(19, params.get(AnalyticsManager.KeyType.PLACE_HIT_TYPE));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY.equalsIgnoreCase(screenName) == true)
        {
            recordScreen(activity, screenName, screenClassOverride);
        } else if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(5, params.get(AnalyticsManager.KeyType.MEMBER_TYPE));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.TRUE_REVIEW_LIST.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            if (DailyHotel.isLogin() == true)
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);
            } else
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);
            }

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(13, params.get(AnalyticsManager.KeyType.CATEGORY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.PEEK_POP.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            if (DailyHotel.isLogin() == true)
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);
            } else
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);
            }

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(13, params.get(AnalyticsManager.KeyType.CATEGORY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.TRUE_VR.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            if (DailyHotel.isLogin() == true)
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);
            } else
            {
                screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);
            }

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(13, params.get(AnalyticsManager.KeyType.CATEGORY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_HOTEL_DOMESTIC_SUBWAY_LIST.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(5, params.get(AnalyticsManager.KeyType.IS_SIGNED));

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(13, params.get(AnalyticsManager.KeyType.CATEGORY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.STAY_LIST_SHORTCUT_HOTEL.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_BOUTIQUE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_PENSION.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_RESORT.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(5, params.get(AnalyticsManager.KeyType.IS_SIGNED));

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(13, params.get(AnalyticsManager.KeyType.CATEGORY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.SEARCHSCREENVIEW_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(3, params.get(AnalyticsManager.KeyType.DBENEFIT));
            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
            screenViewBuilder.setCustomDimension(14, params.get(AnalyticsManager.KeyType.GRADE));
            screenViewBuilder.setCustomDimension(15, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            screenViewBuilder.setCustomDimension(16, params.get(AnalyticsManager.KeyType.LIST_INDEX));
            screenViewBuilder.setCustomDimension(17, params.get(AnalyticsManager.KeyType.RATING));
            screenViewBuilder.setCustomDimension(20, params.get(AnalyticsManager.KeyType.PLACE_COUNT));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE_OUTBOUND.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(1, params.get(AnalyticsManager.KeyType.CHECK_IN));
            screenViewBuilder.setCustomDimension(2, params.get(AnalyticsManager.KeyType.CHECK_OUT));
            screenViewBuilder.setCustomDimension(10, params.get(AnalyticsManager.KeyType.NRD));
            screenViewBuilder.setCustomDimension(14, params.get(AnalyticsManager.KeyType.GRADE));
            screenViewBuilder.setCustomDimension(18, params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENTCOMPLETE_OUTBOUND.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.DAILYHOTEL_THANKYOU_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(4, params.get(AnalyticsManager.KeyType.PAYMENT_TYPE));
            screenViewBuilder.setCustomDimension(9, params.get(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD));

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        } else if (AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT.equalsIgnoreCase(screenName) == true//
            || AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_TEMPORARY_ACCOUNT.equalsIgnoreCase(screenName) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));

            if (params.containsKey(AnalyticsManager.KeyType.COUNTRY) == true)
            {
                screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
            }

            if (params.containsKey(AnalyticsManager.KeyType.PLACE_INDEX) == true)
            {
                screenViewBuilder.setCustomDimension(15, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            }

            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        long value = 0L;

        if (DailyTextUtils.isTextEmpty(category, action) == true)
        {
            return;
        }

        if (AnalyticsManager.Category.NAVIGATION_.equalsIgnoreCase(category) == true//
            && (AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true//
            || AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true))
        {
            label = params.get(AnalyticsManager.KeyType.SCREEN) + '-' + label;
        } else if (AnalyticsManager.Category.POPUP_BOXES.equalsIgnoreCase(category) == true//
            && (AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED.equalsIgnoreCase(action) == true //
            || AnalyticsManager.Action.GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED.equalsIgnoreCase(action) == true))
        {
            String countString = params.get(AnalyticsManager.KeyType.SEARCH_COUNT);
            if (DailyTextUtils.isTextEmpty(countString) == false)
            {
                try
                {
                    value = Long.parseLong(countString);
                } catch (Exception e)
                {
                    value = 0L;
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_BOOKINGS.equalsIgnoreCase(category) == true//
            && AnalyticsManager.Action.GOURMET_MENU_DETAIL_CLICK.equalsIgnoreCase(action) && params != null)
        {
            String where = params.get(AnalyticsManager.KeyType.VALUE);

            try
            {
                value = Long.parseLong(where);
            } catch (Exception e)
            {
                value = 0L;
            }
        }

        mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
            .setCategory(category).setAction(action)//
            .setLabel(label).setValue(value).build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + value);
        }
    }

    @Override
    void recordEvent(String category, String action, String label, long value, Map<String, String> params)
    {
        mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
            .setCategory(category).setAction(action)//
            .setLabel(label).setValue(value).build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + value);
        }
    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || dailyDeepLink.isExternalDeepLink() == false)
        {
            return;
        }

        DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

        String screenName = null;

        if (externalDeepLink.isHotelListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_LIST;
        } else if (externalDeepLink.isHotelDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_DETAIL;
            //        } else if (dailyDeepLink.isHotelRegionListView() == true)
            //        {
            //            screenName = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;
            //        } else if (dailyDeepLink.isHotelEventBannerWebView() == true)
            //        {
            //            screenName = AnalyticsManager.Screen.DAILYHOTEL_BANNER_DETAIL;
        } else if (externalDeepLink.isGourmetListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_LIST;
        } else if (externalDeepLink.isGourmetDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_DETAIL;
            //        } else if (dailyDeepLink.isGourmetRegionListView() == true)
            //        {
            //            screenName = AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC;
            //        } else if (dailyDeepLink.isGourmetEventBannerWebView() == true)
            //        {
            //            screenName = AnalyticsManager.Screen.DAILYGOURMET_BANNER_DETAIL;
        } else if (externalDeepLink.isBookingView() == true)
        {
            screenName = AnalyticsManager.Screen.BOOKING_LIST;
        } else if (externalDeepLink.isEventView() == true)
        {
            screenName = AnalyticsManager.Screen.EVENT_LIST;
        } else if (externalDeepLink.isEventDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.EVENT_DETAIL;
        } else if (externalDeepLink.isBonusView() == true)
        {
            screenName = AnalyticsManager.Screen.BONUS;
        } else if (externalDeepLink.isSingUpView() == true)
        {
            screenName = AnalyticsManager.Screen.MENU_REGISTRATION;
        } else if (externalDeepLink.isInformationView() == true)
        {

        } else if (externalDeepLink.isCouponView() == true)
        {
            screenName = AnalyticsManager.Screen.MENU_COUPON_BOX;
        } else if (externalDeepLink.isRegisterCouponView() == true)
        {
            //            screenName = AnalyticsManager.Screen.
        }

        if (DailyTextUtils.isTextEmpty(screenName) == false)
        {
            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder()//
                .setCampaignParamsFromUrl(dailyDeepLink.getDeepLink()).build());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserInformation(String index, String userType)
    {
        if (DailyTextUtils.isTextEmpty(index) == true)
        {
            mGoogleAnalyticsTracker.set("&uid", AnalyticsManager.ValueType.EMPTY);
        } else
        {
            mGoogleAnalyticsTracker.set("&uid", index);
        }
    }

    @Override
    void setUserBirthday(String birthday)
    {

    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {

    }

    @Override
    void onActivityCreated(Activity activity, Bundle bundle)
    {

    }

    @Override
    void onActivityStarted(Activity activity)
    {

    }

    @Override
    void onActivityStopped(Activity activity)
    {

    }

    @Override
    void onActivityResumed(Activity activity)
    {

    }

    @Override
    void onActivityPaused(Activity activity)
    {

    }

    @Override
    void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {

    }

    @Override
    void onActivityDestroyed(Activity activity)
    {

    }

    @Override
    void currentAppVersion(String version)
    {

    }

    @Override
    void addCreditCard(String cardType)
    {
    }

    @Override
    void updateCreditCard(String cardTypes)
    {

    }

    @Override
    void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen)
    {
    }

    @Override
    void signUpDailyUser(String userIndex, String birthday, String userType, String recommender, String callByScreen)
    {
    }

    @Override
    void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {
        double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        Product product = getProduct(params);
        product.setBrand("hotel");

        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
            .setTransactionId(aggregationId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format(Locale.KOREA, "credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_COMPLETE);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        //
        //        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
        //            .setCheckoutStep(5)//
        //            .setTransactionId(aggregationId)//
        //            .setTransactionRevenue(paymentPrice)//
        //            .setTransactionCouponCode(String.format("credit_%s", credit));
        //
        //        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);
        //
        //        mGoogleAnalyticsTracker.set("&cu", "KRW");
        //        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

        String placeName = params.get(AnalyticsManager.KeyType.NAME);

        recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, AnalyticsManager.Action.HOTEL_PAYMENT_COMPLETED, placeName, null);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : 5 | " + aggregationId + " | " + productAction.toString());
        }
    }

    @Override
    void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {

    }

    @Override
    void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
    {
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);
        double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));

        Product product = getProduct(params);
        product.setBrand("gourmet");

        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
            .setTransactionId(aggregationId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format(Locale.KOREA, "credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_COMPLETE);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        //        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
        //            .setCheckoutStep(5)//
        //            .setTransactionId(aggregationId)//
        //            .setTransactionRevenue(paymentPrice)//
        //            .setTransactionCouponCode(String.format("credit_%s", credit));
        //
        //        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);
        //
        //        mGoogleAnalyticsTracker.set("&cu", "KRW");
        //        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

        String label = params.get(AnalyticsManager.KeyType.LABEL);
        recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.GOURMET_PAYMENT_COMPLETED, label, null);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : 5 | " + aggregationId + " | " + productAction.toString());
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
    void setPushEnabled(boolean onOff, String pushSettingType)
    {

    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {

    }

    private Product getProduct(Map<String, String> params)
    {
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String category = params.get(AnalyticsManager.KeyType.CATEGORY);

        String price = params.get(AnalyticsManager.KeyType.PRICE);
        //        String paymentPrice = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        String quantity = params.get(AnalyticsManager.KeyType.QUANTITY);

        //        String credit = params.get(AnalyticsManager.KeyType.USED_BONUS);

        String id;

        if (DailyTextUtils.isTextEmpty(placeIndex) == false)
        {
            id = placeIndex;
        } else
        {
            return null;
        }

        Product product = new Product().setId(id);

        String name = null;

        if (DailyTextUtils.isTextEmpty(placeName) == false)
        {
            name = placeName;
        }

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            product.setName(name);
        }

        if (DailyTextUtils.isTextEmpty(grade) == false)
        {
            product.setCategory(grade);
        }

        if (DailyTextUtils.isTextEmpty(category) == false)
        {
            product.setCategory(category);
        }

        if (DailyTextUtils.isTextEmpty(price) == false)
        {
            try
            {
                product.setPrice(Double.parseDouble(price));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        if (DailyTextUtils.isTextEmpty(quantity) == false)
        {
            try
            {
                product.setQuantity(Integer.parseInt(quantity));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Product : " + product.toString());
        }

        return product;
    }

    private HitBuilders.ScreenViewBuilder getScreenViewBuilder(Map<String, String> params, Product product, ProductAction productAction)
    {
        HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction);

        String checkIn = null;

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE);
        }

        String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);
        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT);
        String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE);
        String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
        String registeredSimpleCard = params.get(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD);
        String nrd = params.get(AnalyticsManager.KeyType.NRD);
        String category = params.get(AnalyticsManager.KeyType.CATEGORY);
        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX);
        String rating = params.get(AnalyticsManager.KeyType.RATING);
        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE);
        String placeCount = params.get(AnalyticsManager.KeyType.PLACE_COUNT);
        String dailyChoice = params.get(AnalyticsManager.KeyType.DAILYCHOICE);

        if (DailyTextUtils.isTextEmpty(checkIn) == false)
        {
            screenViewBuilder.setCustomDimension(1, checkIn);
        }

        if (DailyTextUtils.isTextEmpty(checkOut) == false)
        {
            screenViewBuilder.setCustomDimension(2, checkOut);
        }

        if (DailyTextUtils.isTextEmpty(dBenefit) == false)
        {
            screenViewBuilder.setCustomDimension(3, "yes".equalsIgnoreCase(dBenefit) == true ? "y" : "n");
        }

        if (DailyTextUtils.isTextEmpty(paymentType) == false)
        {
            screenViewBuilder.setCustomDimension(4, paymentType);
        }

        if (DailyTextUtils.isTextEmpty(placeType) == false)
        {
            screenViewBuilder.setCustomDimension(6, placeType);
        }

        if (DailyTextUtils.isTextEmpty(registeredSimpleCard) == false)
        {
            screenViewBuilder.setCustomDimension(9, registeredSimpleCard);
        }

        if (DailyTextUtils.isTextEmpty(nrd) == false)
        {
            screenViewBuilder.setCustomDimension(10, nrd);
        }

        if (DailyTextUtils.isTextEmpty(category) == false)
        {
            screenViewBuilder.setCustomDimension(13, category);
        }

        if (DailyTextUtils.isTextEmpty(grade) == false)
        {
            screenViewBuilder.setCustomDimension(14, grade);
        }

        if (DailyTextUtils.isTextEmpty(placeIndex) == false)
        {
            screenViewBuilder.setCustomDimension(15, placeIndex);
        }

        if (DailyTextUtils.isTextEmpty(listIndex) == false && "-1".equalsIgnoreCase(listIndex) == false)
        {
            screenViewBuilder.setCustomDimension(16, listIndex);
        }

        if (DailyTextUtils.isTextEmpty(rating) == false)
        {
            screenViewBuilder.setCustomDimension(17, rating + "%");
        }

        if (DailyTextUtils.isTextEmpty(isShowOriginalPrice) == false)
        {
            screenViewBuilder.setCustomDimension(18, isShowOriginalPrice.toLowerCase());
        }

        if (DailyTextUtils.isTextEmpty(dailyChoice) == false)
        {
            screenViewBuilder.setCustomDimension(19, dailyChoice.toLowerCase());
        }

        if (DailyTextUtils.isTextEmpty(placeCount) == false && "-1".equalsIgnoreCase(placeCount) == false)
        {
            screenViewBuilder.setCustomDimension(20, placeCount.toLowerCase());
        }

        return screenViewBuilder;
    }

    private void checkoutStep(int step, String screen, String aggregationId, Map<String, String> params)
    {
        String paymentPrice = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        Product product = getProduct(params);

        ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT).setCheckoutStep(step);

        if (DailyTextUtils.isTextEmpty(aggregationId) == false)
        {
            productAction.setTransactionId(aggregationId);
        }

        if (DailyTextUtils.isTextEmpty(paymentPrice) == false)
        {
            productAction.setTransactionRevenue(Double.parseDouble(paymentPrice));
        }

        if (DailyTextUtils.isTextEmpty(credit) == false)
        {
            productAction.setTransactionCouponCode(String.format(Locale.KOREA, "credit_%s", credit));
        }

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(screen);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : " + screen + " | " + step + " | " + aggregationId + " | " + productAction.toString() + " | " + screenViewBuilder.build().toString());
        }
    }

    private void recordSearchAnalytics(String screenName, Map<String, String> params)
    {
        if (DailyTextUtils.isTextEmpty(screenName) == true || params == null)
        {
            return;
        }

        HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

        String checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
        String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);

        if (DailyTextUtils.isTextEmpty(checkIn) == false)
        {
            screenViewBuilder.setCustomDimension(1, checkIn);
        }

        if (DailyTextUtils.isTextEmpty(checkOut) == false)
        {
            screenViewBuilder.setCustomDimension(2, checkOut);
        }

        screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
        screenViewBuilder.setCustomDimension(19, params.get(AnalyticsManager.KeyType.PLACE_HIT_TYPE));
        screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
        screenViewBuilder.setCustomDimension(8, params.get(AnalyticsManager.KeyType.PROVINCE));

        String district = params.get(AnalyticsManager.KeyType.DISTRICT);
        if (DailyTextUtils.isTextEmpty(district) == false)
        {
            screenViewBuilder.setCustomDimension(12, district);
        }

        String category = params.get(AnalyticsManager.KeyType.CATEGORY);
        if (DailyTextUtils.isTextEmpty(category) == false)
        {
            screenViewBuilder.setCustomDimension(13, category);
        }

        mGoogleAnalyticsTracker.setScreenName(screenName);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "recordScreen : " + screenName + " | " + screenViewBuilder.build().toString());
        }
    }
}
