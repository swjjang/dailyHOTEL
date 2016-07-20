package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;

import java.util.Map;

public abstract class BaseAnalyticsManager
{
    abstract void recordScreen(String screen);

    abstract void recordScreen(String screen, Map<String, String> params);

    abstract void recordEvent(String category, String action, String label, Map<String, String> params);

    abstract void recordEvent(Map<String, String> params);

    abstract void recordDeepLink(String deepLink);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    abstract void setUserIndex(String index);

    abstract void setExceedBonus(boolean isExceedBonus);

    abstract void onStart(Activity activity);

    abstract void onStop(Activity activity);

    abstract void onResume(Activity activity);

    abstract void onPause(Activity activity);

    abstract void currentAppVersion(String version);

    abstract void addCreditCard(String cardType);

    abstract void updateCreditCard(String cardTypes);

    abstract void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType);

    abstract void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender);

    abstract void purchaseCompleteHotel(String transId, Map<String, String> params);

    abstract void purchaseCompleteGourmet(String transId, Map<String, String> params);
}
