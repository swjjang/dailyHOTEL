package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;

import java.util.Map;

public interface IBaseAnalyticsManager
{
    void setEnabled(boolean enabled);

    void recordScreen(String screen, Map<String, String> params);

    void recordEvent(String category, String action, String label, Map<String, String> params);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    void setUserIndex(String index);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void addCreditCard(String cardType);

    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType);

    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType);

    void purchaseCompleteHotel(String transId, Map<String, String> params);

    void purchaseCompleteGourmet(String transId, Map<String, String> params);
}
