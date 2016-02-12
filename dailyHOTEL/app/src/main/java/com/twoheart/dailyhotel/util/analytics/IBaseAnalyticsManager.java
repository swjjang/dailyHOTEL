package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

public interface IBaseAnalyticsManager
{
    abstract void recordScreen(String screenName, Map<String, String> params);

    abstract void recordEvent(String eventName, String action, String label, Long value);

    abstract void recordEvent(String eventName, String action, String label, Map<String, String> params);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    abstract void setUserIndex(String index);

    abstract void onResume(Activity activity);

    abstract void onPause(Activity activity);

    abstract void addCreditCard(String cardType);

    abstract void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType);

    abstract void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType);

    abstract void purchaseCompleteHotel(String transId, Map<String, String> params);

    abstract void purchaseCompleteGourmet(String transId, Map<String, String> params);
}
