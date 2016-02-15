package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;

import java.util.Map;

public interface IBaseAnalyticsManager
{
    abstract void recordScreen(String screen, Map<String, String> params);

    abstract void recordEvent(String category, String action, String label, Map<String, String> params);

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

    // 예약 진행화면에서 이탈한 경우(결제를 취소한 경우)
    abstract void initiatedCheckoutHotel(Map<String, String> params);

    abstract void initiatedCheckoutGourmet(Map<String, String> params);
}
