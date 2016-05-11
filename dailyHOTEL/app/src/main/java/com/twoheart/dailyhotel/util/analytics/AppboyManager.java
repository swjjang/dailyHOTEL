package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.twoheart.dailyhotel.util.Constants;
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
        appboyProperties.addProperty("user_idx", getUserIndex());

        mAppboy.logCustomEvent(EventName.SCREEN);
    }

    @Override
    public void recordScreen(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.BOOKING_LIST.equalsIgnoreCase(screen) == true)
        {
            AppboyProperties appboyProperties = new AppboyProperties();

            appboyProperties.addProperty(screen, "");
            appboyProperties.addProperty("user_idx", getUserIndex());

            String intValue1 = params.get(AnalyticsManager.KeyType.NUM_OF_BOOKING);
            appboyProperties.addProperty(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.parseInt(intValue1));

            mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
        } else
        {
            AppboyProperties appboyProperties = getAppboyProperties(params);

            if (appboyProperties != null)
            {
                appboyProperties.addProperty(screen, "");
                appboyProperties.addProperty("user_idx", getUserIndex());

                mAppboy.logCustomEvent(EventName.SCREEN, appboyProperties);
            }
        }
    }

    @Override
    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setUserIndex(String index)
    {
        mUserIndex = index;
        mAppboy.changeUser(index);
    }

    @Override
    public void onStart(Activity activity)
    {

    }

    @Override
    public void onStop(Activity activity)
    {

    }

    @Override
    public void onResume(Activity activity)
    {
    }

    @Override
    public void onPause(Activity activity)
    {
    }

    @Override
    public void addCreditCard(String cardType)
    {
    }

    @Override
    public void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    public void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType)
    {
    }

    @Override
    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
    }

    @Override
    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
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
}
