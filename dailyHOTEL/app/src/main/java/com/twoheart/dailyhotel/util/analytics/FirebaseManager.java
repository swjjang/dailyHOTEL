package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[FirebaseManager]";

    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseManager(Context context)
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, screenClassOverride);
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, screenClassOverride);
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (AnalyticsManager.Category.SHARE.equals(category) == true)
        {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                bundle.putString(entry.getKey(), entry.getValue());
            }

            mFirebaseAnalytics.logEvent(category, bundle);
        }
    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserInformation(String index, String userType)
    {
    }

    @Override
    void setUserBirthday(String birthday)
    {
    }

    @Override
    void setUserName(String name)
    {
    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {
    }

    @Override
    void onActivityCreated(Activity activity, Bundle bundle)
    {
    }

    @Override
    void onActivityStarted(Activity activity)
    {
    }

    @Override
    void onActivityStopped(Activity activity)
    {
    }

    @Override
    void onActivityResumed(Activity activity)
    {
    }

    @Override
    void onActivityPaused(Activity activity)
    {
    }

    @Override
    void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {
    }

    @Override
    void onActivityDestroyed(Activity activity)
    {
    }

    @Override
    void currentAppVersion(String version)
    {
    }

    @Override
    void addCreditCard(String cardType)
    {
    }

    @Override
    void updateCreditCard(String cardTypes)
    {
    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber,//
                          String userType, String callByScreen)
    {
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber,//
                         String birthday, String userType, String recommender, String callByScreen)
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

    @Override
    void startDeepLink(Uri deepLinkUri)
    {

    }

    @Override
    void startApplication()
    {

    }

    @Override
    void onRegionChanged(String country, String provinceName)
    {

    }

    @Override
    void setPushEnabled(boolean enabled, String pushSettingType)
    {
    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {

    }
}
