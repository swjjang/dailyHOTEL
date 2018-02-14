package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.daily.base.util.DailyTextUtils;
import com.kakao.ad.common.json.CompleteRegistration;
import com.kakao.ad.common.json.Event;
import com.kakao.ad.tracker.KakaoAdTracker;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.Map;

public class KakaoManager extends BaseAnalyticsManager
{
    Context mContext;

    public KakaoManager(Context context)
    {
        KakaoAdTracker.getInstance().init(context, "306637043627835071");
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {

    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        if(activity == null || DailyTextUtils.isTextEmpty(screenName) == true)
        {
            return;
        }

        switch(screenName)
        {
            case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                break;

            case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                break;

            case AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND:
                break;
        }

    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {

    }

    @Override
    void recordEvent(String category, String action, String label, long value, Map<String, String> params)
    {

    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

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
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType, String callByScreen)
    {
        Event event = new CompleteRegistration();
        event.tag = "CompleteRegistration";

        KakaoAdTracker.getInstance().sendEvent(event);
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String birthday, String userType, String recommender, String callByScreen)
    {
        Event event = new CompleteRegistration();
        event.tag = "CompleteRegistration";

        KakaoAdTracker.getInstance().sendEvent(event);
    }

    @Override
    void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {

    }

    @Override
    void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {

    }

    @Override
    void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
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
    void setPushEnabled(boolean onOff, String pushSettingType)
    {

    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {

    }

    @Override
    void onSearch(String keyword, String autoKeyword, String category, int resultCount)
    {

    }
}
