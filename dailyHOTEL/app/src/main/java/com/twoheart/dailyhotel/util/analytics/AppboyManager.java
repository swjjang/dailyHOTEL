package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.appboy.ui.inappmessage.AppboyInAppMessageManager;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class AppboyManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[AppboyManager]";

    private Appboy mAppboy;
    private String mUserIndex;
    private boolean mRefreshData;

    public AppboyManager(Context context)
    {
        mAppboy = Appboy.getInstance(context);
    }

    @Override
    void recordScreen(String screen)
    {
        AppboyProperties appboyProperties = new AppboyProperties();
        appboyProperties.addProperty(screen, AnalyticsManager.ValueType.EMPTY);
        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());

        mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
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

            appboyProperties.addProperty(screen, AnalyticsManager.ValueType.EMPTY);
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
        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screen) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.HOTEL_CATEGORY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWD_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.UNIT_PRICE, Integer.parseInt(params.get(AnalyticsManager.KeyType.UNIT_PRICE)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_IN_DATE))));

                mAppboy.logCustomEvent(EventName.STAY_DETAIL_CICKED, appboyProperties);
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screen) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWD_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.UNIT_PRICE, Integer.parseInt(params.get(AnalyticsManager.KeyType.UNIT_PRICE)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.VISIT_DATE))));

                mAppboy.logCustomEvent(EventName.GOURMET_DETAIL_CLICKED, appboyProperties);
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
            }
        } else
        {
            AppboyProperties appboyProperties = getAppboyProperties(params);

            if (appboyProperties != null)
            {
                appboyProperties.addProperty(screen, AnalyticsManager.ValueType.EMPTY);
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
            } else if (AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                satisfactionCustomEvent(label);
            } else if (AnalyticsManager.Action.HOTEL_DISSATISFACTION_DETAILED_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.SELECTED_RESPONSE_ITEM, label);
                appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.TICKET_NAME));

                mAppboy.logCustomEvent(EventName.STAY_DISSATISFACTION_DETAIL_RESPONSE, appboyProperties);
            } else if (AnalyticsManager.Action.GOURMET_DISSATISFACTION_DETAILED_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.SELECTED_RESPONSE_ITEM, label);
                appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.TICKET_NAME));

                mAppboy.logCustomEvent(EventName.GOURMET_DISSATISFACTION_DETAIL_RESPONSE, appboyProperties);
            }
        } else if (AnalyticsManager.Category.NAVIGATION.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWD_DATE, new Date());

                try
                {
                    appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_IN_DATE))));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE))));
                    appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY)));

                    mAppboy.logCustomEvent(EventName.STAY_SELECTED_DATE, appboyProperties);
                } catch (NumberFormatException e)
                {
                    ExLog.d(e.toString());
                }
            } else if (AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true)
            {
                AppboyProperties appboyProperties = new AppboyProperties();

                appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
                appboyProperties.addProperty(AnalyticsManager.KeyType.VIEWD_DATE, new Date());

                try
                {
                    appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.VISIT_DATE))));

                    mAppboy.logCustomEvent(EventName.GOURMET_SELECTED_DATE, appboyProperties);
                } catch (NumberFormatException e)
                {
                    ExLog.d(e.toString());
                }
            }
        } else if (AnalyticsManager.Category.HOTEL_BOOKINGS.equalsIgnoreCase(category) == true//
            && AnalyticsManager.Action.BOOKING_CLICKED.equalsIgnoreCase(action) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.HOTEL_CATEGORY));
            appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
            appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
            appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
            appboyProperties.addProperty(AnalyticsManager.KeyType.BOOKING_INITIALISED_DATE, new Date());

            try
            {
                appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM)));
                appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_IN_DATE))));

                mAppboy.logCustomEvent(EventName.STAY_BOOKING_INITIALISED, appboyProperties);
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
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
                appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.VISIT_DATE))));

                mAppboy.logCustomEvent(EventName.GOURMET_BOOKING_INITIALISED, appboyProperties);
            } catch (NumberFormatException e)
            {
                ExLog.d(e.toString());
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
        }

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
        if (mAppboy.openSession(activity))
        {
            mRefreshData = true;
        }
    }

    @Override
    void onStop(Activity activity)
    {
        mAppboy.closeSession(activity);
    }

    @Override
    void onResume(Activity activity)
    {
        AppboyInAppMessageManager.getInstance().registerInAppMessageManager(activity);
        if (mRefreshData)
        {
            mAppboy.requestInAppMessageRefresh();
            mRefreshData = false;
        }
    }

    @Override
    void onPause(Activity activity)
    {
        AppboyInAppMessageManager.getInstance().unregisterInAppMessageManager(activity);
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
            cardTypes = AnalyticsManager.ValueType.EMPTY;
        }

        appboyProperties.addProperty(AnalyticsManager.KeyType.CARD_ISSUING_COMPANY, cardTypes);

        mAppboy.logCustomEvent(EventName.REGISTERED_CARD_INFO, appboyProperties);
    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, userIndex);
        appboyProperties.addProperty(AnalyticsManager.KeyType.TYPE_OF_REGISTRATION, AnalyticsManager.UserType.EMAIL);
        appboyProperties.addProperty(AnalyticsManager.KeyType.REGISTRATION_DATE, new Date());

        if (Util.isTextEmpty(recommender) == true)
        {
            recommender = AnalyticsManager.ValueType.EMPTY;
        }

        appboyProperties.addProperty(AnalyticsManager.KeyType.REFERRAL_CODE, recommender);

        mAppboy.logCustomEvent(EventName.REGISTER_COMPLETED, appboyProperties);
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_CATEGORY, params.get(AnalyticsManager.KeyType.HOTEL_CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.STAY_NAME, params.get(AnalyticsManager.KeyType.NAME));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        try
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_IN_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_IN_DATE))));
            appboyProperties.addProperty(AnalyticsManager.KeyType.CHECK_OUT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.CHECK_OUT_DATE))));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));

            mAppboy.logPurchase(EventName.STAY_PURCHASE_COMPLETED, "KRW", new BigDecimal(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), appboyProperties);
        } catch (NumberFormatException e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        AppboyProperties appboyProperties = new AppboyProperties();

        appboyProperties.addProperty(AnalyticsManager.KeyType.USER_IDX, getUserIndex());
        appboyProperties.addProperty(AnalyticsManager.KeyType.GOURMET_CATEGORY, params.get(AnalyticsManager.KeyType.CATEGORY));
        appboyProperties.addProperty(AnalyticsManager.KeyType.RESTAURANT_NAME, params.get(AnalyticsManager.KeyType.NAME));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PROVINCE, params.get(AnalyticsManager.KeyType.PROVINCE));
        appboyProperties.addProperty(AnalyticsManager.KeyType.DISTRICT, params.get(AnalyticsManager.KeyType.DISTRICT));
        appboyProperties.addProperty(AnalyticsManager.KeyType.AREA, params.get(AnalyticsManager.KeyType.AREA));
        appboyProperties.addProperty(AnalyticsManager.KeyType.PURCHASED_DATE, new Date());

        try
        {
            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_HOUR, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.VISIT_HOUR))));
            appboyProperties.addProperty(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.parseInt(params.get(AnalyticsManager.KeyType.PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.REVENUE, Integer.parseInt(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.VISIT_DATE, new Date(Long.parseLong(params.get(AnalyticsManager.KeyType.VISIT_DATE))));
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_TICKETS, Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
            appboyProperties.addProperty(AnalyticsManager.KeyType.USED_CREDITS, Integer.parseInt(params.get(AnalyticsManager.KeyType.USED_BOUNS)));

            mAppboy.logPurchase(EventName.GOURMET_PURCHASE_COMPLETED, "KRW", new BigDecimal(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)), appboyProperties);
        } catch (NumberFormatException e)
        {
            ExLog.d(e.toString());
        }
    }

    private String getUserIndex()
    {
        return Util.isTextEmpty(mUserIndex) == true ? AnalyticsManager.ValueType.EMPTY : mUserIndex;
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
            String value = Util.isTextEmpty(element.getValue()) == true ? AnalyticsManager.ValueType.EMPTY : element.getValue();
            appboyProperties.addProperty(element.getKey(), element.getValue());
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
        public static final String STAY_DISSATISFACTION_DETAIL_RESPONSE = "stay_dissatisfaction_detail_response";
        public static final String GOURMET_SATISFACTION_SURVEY = "gourmet_satisfaction_survey";
        public static final String GOURMET_DISSATISFACTION_DETAIL_RESPONSE = "gourmet_dissatisfaction_detail_response";


    }

    private static final class ValueName
    {
        public static final String DAILYHOTEL = "dailyhotel";
        public static final String DAILYGOURMET = "dailygourmet";
        public static final String SATISFIED = "satisfied";
        public static final String DISSATISFIED = "dissatisfied";
        public static final String CLOSED = "closed";
    }
}
