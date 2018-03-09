package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.Map;

public abstract class BaseAnalyticsManager
{
    abstract void recordScreen(Activity activity, String screenName, String screenClassOverride);

    abstract void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params);

    abstract void recordEvent(String category, String action, String label, Map<String, String> params);

    abstract void recordEvent(String category, String action, String label, long value, Map<String, String> params);

    abstract void recordDeepLink(DailyDeepLink dailyDeepLink);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    abstract void setUserInformation(String index, String userType);

    abstract void setUserBirthday(String birthday);

    abstract void setExceedBonus(boolean isExceedBonus);

    abstract void onActivityCreated(Activity activity, Bundle bundle);

    abstract void onActivityStarted(Activity activity);

    abstract void onActivityStopped(Activity activity);

    abstract void onActivityResumed(Activity activity);

    abstract void onActivityPaused(Activity activity);

    abstract void onActivitySaveInstanceState(Activity activity, Bundle bundle);

    abstract void onActivityDestroyed(Activity activity);

    abstract void currentAppVersion(String version);

    abstract void addCreditCard(String cardType);

    abstract void updateCreditCard(String cardTypes);

    abstract void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen);

    abstract void signUpDailyUser(String userIndex, String birthday, String userType, String recommender, String callByScreen);

    abstract void purchaseCompleteHotel(String aggregationId, Map<String, String> params);

    abstract void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params);

    abstract void purchaseCompleteGourmet(String aggregationId, Map<String, String> params);

    abstract void startDeepLink(Uri deepLinkUri);

    abstract void startApplication();

    abstract void onRegionChanged(String country, String provinceName);

    abstract void setPushEnabled(boolean onOff, String pushSettingType);

    abstract void purchaseWithCoupon(Map<String, String> param);
}
