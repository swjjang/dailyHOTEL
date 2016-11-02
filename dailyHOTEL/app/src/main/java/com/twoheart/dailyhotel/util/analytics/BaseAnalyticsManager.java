package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.net.Uri;

import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.Map;

public abstract class BaseAnalyticsManager
{
    abstract void recordScreen(String screen);

    abstract void recordScreen(String screen, Map<String, String> params);

    abstract void recordEvent(String category, String action, String label, Map<String, String> params);

    abstract void recordDeepLink(DailyDeepLink dailyDeepLink);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    abstract void setUserInformation(String index, String userType);

    abstract void setUserBirthday(String birthday);

    abstract void setUserName(String name);

    abstract void setExceedBonus(boolean isExceedBonus);

    abstract void onStart(Activity activity);

    abstract void onStop(Activity activity);

    abstract void onResume(Activity activity);

    abstract void onPause(Activity activity);

    abstract void currentAppVersion(String version);

    abstract void addCreditCard(String cardType);

    abstract void updateCreditCard(String cardTypes);

    abstract void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType, String callByScreen);

    abstract void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String birthday, String userType, String recommender, String callByScreen);

    abstract void purchaseCompleteHotel(String transId, Map<String, String> params);

    abstract void purchaseCompleteGourmet(String transId, Map<String, String> params);

    abstract void startDeepLink(Uri deepLinkUri);

    abstract void startApplication();

    abstract void onRegionChanged(String country, String provinceName);

    abstract void setPushEnabled(boolean onOff, String pushSettingType);

    abstract void purchaseWithCoupon(Map<String, String> param);
}
