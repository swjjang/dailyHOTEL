package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.Map;

public class FacebookManager implements IBaseAnalyticsManager
{
    private Context mContext;

    public FacebookManager(Context context)
    {
        mContext = context;
    }

    @Override
    public void recordScreen(String screenName, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        try
        {
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
                parameters.putString(EventParam.TOTAL_PRICE, params.get(AnalyticsManager.KeyType.PRICE));
                parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));

                appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, parameters);
            } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
            {
                AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

                Bundle parameters = new Bundle();
                parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
                parameters.putString(EventParam.RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
                parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
                parameters.putString(EventParam.TOTAL_PRICE, params.get(AnalyticsManager.KeyType.PRICE));
                parameters.putString(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, params.get(AnalyticsManager.KeyType.QUANTITY));

                appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, parameters);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void recordEvent(String eventName, String action, String label, Map<String, String> params)
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setUserIndex(String index)
    {
    }

    @Override
    public void onResume(Activity activity)
    {
        AppEventsLogger.activateApp(activity);
    }

    @Override
    public void onPause(Activity activity)
    {
        AppEventsLogger.deactivateApp(activity);
    }

    @Override
    public void addCreditCard(String cardType)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(EventParam.CARD_TYPE, cardType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO, parameters);
    }

    @Override
    public void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, userType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);
    }

    @Override
    public void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, userType);

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);
    }

    @Override
    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.HOTEL);
        parameters.putString(EventParam.CHECK_IN_DATE, params.get(AnalyticsManager.KeyType.CHECK_IN));
        parameters.putString(EventParam.CHECK_OUT_DATE, params.get(AnalyticsManager.KeyType.CHECK_OUT));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
        parameters.putString(EventParam.TOTAL_PRICE, params.get(AnalyticsManager.KeyType.TOTAL_PRICE));
        parameters.putString(EventParam.NUMBER_OF_NIGHTS, params.get(AnalyticsManager.KeyType.QUANTITY));

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, parameters);
    }

    @Override
    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(mContext);

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, params.get(AnalyticsManager.KeyType.NAME));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, params.get(AnalyticsManager.KeyType.PLACE_INDEX));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, AnalyticsManager.Label.GOURMET);
        parameters.putString(EventParam.RESERVATION_DATE, params.get(AnalyticsManager.KeyType.DATE));
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "KRW");
        parameters.putString(EventParam.TOTAL_PRICE, params.get(AnalyticsManager.KeyType.TOTAL_PRICE));
        parameters.putString(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, params.get(AnalyticsManager.KeyType.QUANTITY));

        appEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, parameters);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected static final class EventParam
    {
        public static final String CHECK_IN_DATE = "Check in Date";
        public static final String CHECK_OUT_DATE = "Check out Date";
        public static final String NUMBER_OF_NIGHTS = "Number of Nights";
        public static final String RESERVATION_DATE = "Reservation Date";
        public static final String RESERVATION_TIME = "Reservation Time";
        public static final String TOTAL_PRICE = "valueToSum";
        public static final String CARD_TYPE = "Card Type";
    }
}
