package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

public class FacebookManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[FacebookManager]";

    Context mContext;

    public FacebookManager(Context context)
    {
        mContext = context;

        setDeferredDeepLink();
    }

    private void setDeferredDeepLink()
    {
        AppLinkData.fetchDeferredAppLinkData(mContext, new AppLinkData.CompletionHandler()
        {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData)
            {
                if (appLinkData == null)
                {
                    return;
                }

                Intent intent = new Intent(mContext, LauncherActivity.class);
                intent.setData(appLinkData.getTargetUri());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        if (AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, EventParam.HOTEL_LIST);

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, parameters);

        } else if (AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, EventParam.GOURMET_LIST);

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, parameters);
        }
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.HOTEL);
            parameters.putString(EventParam.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN));
            parameters.putString(EventParam.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.HOTEL_VALUE_TO_SUM, params.get(AnalyticsManager.KeyType.PRICE));
            parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
            parameters.putString(EventParam.GOURMET_RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.GOURMET_VALUE_TO_SUM, params.get(AnalyticsManager.KeyType.PRICE));

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.HOTEL);
            parameters.putString(EventParam.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN));
            parameters.putString(EventParam.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.HOTEL_VALUE_TO_SUM, params.get(AnalyticsManager.KeyType.TOTAL_PRICE));
            parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
            parameters.putString(EventParam.GOURMET_RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.GOURMET_VALUE_TO_SUM, params.get(AnalyticsManager.KeyType.PRICE));

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            String price = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.HOTEL);
            parameters.putString(EventParam.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN));
            parameters.putString(EventParam.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.HOTEL_VALUE_TO_SUM, price);
            parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Currency.getInstance("KRW").getCurrencyCode());

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        } else if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

            String price = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
            parameters.putString(EventParam.GOURMET_RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
            parameters.putString(EventParam.GOURMET_VALUE_TO_SUM, price);
            parameters.putString(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, params.get(AnalyticsManager.KeyType.QUANTITY));
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Currency.getInstance("KRW").getCurrencyCode());

            appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS, parameters);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + parameters.toString());
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (DailyTextUtils.isTextEmpty(category, action, label) == true)
        {
            return;
        }

        if (AnalyticsManager.Category.NAVIGATION_.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.HOTEL_LOCATIONS_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

                Bundle parameters = new Bundle();

                String value = String.format(Locale.KOREA, "%s_%s", mContext.getString(R.string.label_hotel), label.replaceAll("-", "_"));
                parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, value);

                appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, parameters);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + parameters.toString());
                }
            } else if (AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

                Bundle parameters = new Bundle();

                String value = String.format(Locale.KOREA, "%s_%s", mContext.getString(R.string.label_gourmet), label.replaceAll("-", "_"));
                parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, value);

                appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, parameters);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + parameters.toString());
                }
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, long value, Map<String, String> params)
    {

    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserInformation(String index, String userType)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        if (DailyTextUtils.isTextEmpty(index) == true)
        {
            appEventsLogger.logEvent(EventName.LOGIN, 0);
        } else
        {
            appEventsLogger.logEvent(EventName.LOGIN, 1);
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
        AppEventsLogger.activateApp(activity);
    }

    @Override
    void onActivityPaused(Activity activity)
    {
        AppEventsLogger.deactivateApp(activity);
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
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(EventParam.CARD_TYPE, cardType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO, parameters);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "addCreditCard : " + parameters.toString());
        }
    }

    @Override
    void updateCreditCard(String cardTypes)
    {

    }

    @Override
    void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, userType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "signUpSocialUser : " + parameters.toString());
        }
    }

    @Override
    void signUpDailyUser(String userIndex, String birthday, String userType, String recommender, String callByScreen)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, userType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "signUpDailyUser : " + parameters.toString());
        }
    }

    @Override
    void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        String price = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.HOTEL);
        parameters.putString(EventParam.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN));
        parameters.putString(EventParam.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
        parameters.putString(EventParam.HOTEL_VALUE_TO_SUM, price);
        parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));

        appEventsLogger.logPurchase(new BigDecimal(price), Currency.getInstance("KRW"), parameters);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "purchaseCompleteHotel : " + parameters.toString());
        }
    }

    @Override
    void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {

    }

    @Override
    void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        String price = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
        parameters.putString(EventParam.GOURMET_RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
        parameters.putString(EventParam.GOURMET_VALUE_TO_SUM, price);
        parameters.putString(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, params.get(AnalyticsManager.KeyType.QUANTITY));

        appEventsLogger.logPurchase(new BigDecimal(price), Currency.getInstance("KRW"), parameters);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "purchaseCompleteGourmet : " + parameters.toString());
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class EventName
    {
        public static final String LOGIN = "login";
    }

    private static final class EventParam
    {
        public static final String HOTEL_LIST = "HotelList";
        public static final String GOURMET_LIST = "GourmetList";

        public static final String CHECK_IN_DATE = "Check in Date";
        public static final String CHECK_OUT_DATE = "Check out Date";
        public static final String NUMBER_OF_NIGHTS = "Number of Nights";
        public static final String GOURMET_RESERVATION_DATE = "Gourmet Reservation Date";
        public static final String HOTEL_VALUE_TO_SUM = "Hotel valueToSum";
        public static final String GOURMET_VALUE_TO_SUM = "Gourmet valueToSum";
        public static final String CARD_TYPE = "Card Type";
    }
}
