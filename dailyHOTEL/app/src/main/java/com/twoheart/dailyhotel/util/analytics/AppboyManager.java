package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Map;

public class AppboyManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[AppboyManager]";

    private Appboy mAppboy;
    private String mUserIndex;

    public AppboyManager(Context context)
    {
        mAppboy = Appboy.getInstance(context);
    }

    @Override
    void recordScreen(String screen)
    {
        AppboyProperties appboyProperties = new AppboyProperties();
        appboyProperties.addProperty(screen, "");
        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

        mAppboy.logCustomEvent(EventName.SCREEN);
    }

    @Override
    void recordScreen(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.BOOKING_LIST.equalsIgnoreCase(screen) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(screen, "");
            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

            try
            {
                String intValue1 = params.get(AnalyticsManager.KeyType.NUM_OF_BOOKING);
                appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.parseInt(intValue1));

                mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
            }
        } else
        {
            AppboyProperties appboyProperties = getAppboyProperties(params);

            if (appboyProperties != null)
            {
                appboyProperties.addProperty(screen, "");
                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

                mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (AnalyticsManager.Category.HOTEL_SEARCH.equalsIgnoreCase(category) == true//
            && (AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_NOT_FOUND.equalsIgnoreCase(action) == true//
            || AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_CLICKED.equalsIgnoreCase(action) == true))//
        {
            searchCustomEvent(EventName.SEARCH_TERM, ValueName.DAILYHOTEL, params);
        } else if (AnalyticsManager.Category.GOURMET_SEARCH.equalsIgnoreCase(category) == true//
            && (AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_NOT_FOUND.equalsIgnoreCase(action) == true//
            || AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_CLICKED.equalsIgnoreCase(action) == true))//
        {
            searchCustomEvent(EventName.SEARCH_TERM, ValueName.DAILYGOURMET, params);
        } else if (AnalyticsManager.Category.POPUP_BOXES.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED.equalsIgnoreCase(action) == true)
            {
                if (AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.LOWTOHIGH_PRICE_SORTED, ValueName.DAILYHOTEL, params);
                } else if (AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.HIGHTOLOW_PRICE_SORTED, ValueName.DAILYHOTEL, params);
                } else if (AnalyticsManager.Label.SORTFILTER_RATING.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.RATING_SORTED, ValueName.DAILYHOTEL, params);
                }
            } else if (AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED.equalsIgnoreCase(action) == true//
                && AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE.equalsIgnoreCase(label) == true)
            {
                if (AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.LOWTOHIGH_PRICE_SORTED, ValueName.DAILYGOURMET, params);
                } else if (AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.HIGHTOLOW_PRICE_SORTED, ValueName.DAILYGOURMET, params);
                } else if (AnalyticsManager.Label.SORTFILTER_RATING.equalsIgnoreCase(label) == true)
                {
                    curationCustomEvent(EventName.RATING_SORTED, ValueName.DAILYGOURMET, params);
                }
            }
        } else
        {
            if (Util.isTextEmpty(category, action, label) == true)
            {

            }
        }
    }

    @Override
    void recordEvent(Map<String, String> params)
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

            mAppboy.logCustomEvent(EventName.SEARCH_TERM, appboyProperties);
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

        mAppboy.logCustomEvent(eventName, appboyProperties);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserIndex(String index)
    {
        mUserIndex = index;
        mAppboy.changeUser(index);
    }

    @Override
    void onStart(Activity activity)
    {
        mAppboy.openSession(activity);
    }

    @Override
    void onStop(Activity activity)
    {
        mAppboy.openSession(activity);
    }

    @Override
    void onResume(Activity activity)
    {
    }

    @Override
    void onPause(Activity activity)
    {
    }

    @Override
    void currentAppVersion(String version)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.APP_VERSION, version);

        mAppboy.logCustomEvent(EventName.CURRENT_APP_VERSION, appboyProperties);
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


        if (Util.isTextEmpty(cardTypes) == true)
        {
            cardTypes = "";
        }

        appboyProperties.addProperty(AnalyticsManager.KeyType.CARD_ISSUING_COMPANY, cardTypes);

        mAppboy.logCustomEvent(EventName.REGISTERED_CARD_INFO, appboyProperties);
    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType)
    {
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
    }

    private String getUserIndex()
    {
        return Util.isTextEmpty(mUserIndex) == true ? "" : mUserIndex;
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
            appboyProperties.addProperty((String) element.getKey(), element.getValue());
        }

        return appboyProperties;
    }

    private static final class EventName
    {
        public static final String SCREEN = "screen";

        public static final String SEARCH_TERM = "search_term";
        public static final String LOWTOHIGH_PRICE_SORTED = "low_to_high_price_sorted";
        public static final String HIGHTOLOW_PRICE_SORTED = "high_to_low_price_sorted";
        public static final String RATING_SORTED = "rating_sorted";
        public static final String CURRENT_APP_VERSION = "current_app_version";
        public static final String REGISTERED_CARD_INFO = "registered_card_info";
        public static final String STAY_SELECTED_DATE = "stay_selected_date";
        public static final String STAY_DETAIL_CICKED = "stay_detail_clicked";
        public static final String STAY_BOOKING_INITIALISED = "stay_booking_initialised";
        public static final String STAY_PURCHASE_COMPLETED = "stay_purchase_completed";
        public static final String GOURMET_SELECTED_DATE = "gourmet_selected_date";
        public static final String GOURMET_DETAIL_CLICKED = "gourmet_detail_clicked";
        public static final String GOURMET_BOOKING_INITIALISED = "gourmet_booking_initialised";
        public static final String GOURMET_PURCHASE_COMPLETED = "gourmet_purchase_completed";
        public static final String REGISTER_COMPLETED = "register_completed";
        public static final String STAY_SATISFACTION_SURVEY = "stay_satisfaction_survey";
        public static final String STAY_SATISFACTION_RESPONSE = "stay_satisfaction_detail_response";
        public static final String GOURMET_SATISFACTION_SURVEY = "gourmet_satisfaction_survey";
        public static final String GOURMET_STATISFACTION_DETAIL_RESPONSE = "gourmet_satisfaction_detail_response";


    }

    private static final class ValueName
    {
        public static final String DAILYHOTEL = "dailyhotel";
        public static final String DAILYGOURMET = "dailygourmet";
    }
}
