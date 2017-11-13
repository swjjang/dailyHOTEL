package com.daily.dailyhotel.screen.mydaily.profile;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class ProfileAnalyticsImpl implements ProfilePresenter.ProfileAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.PROFILE, null);
    }

    @Override
    public void onScreenLogout(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE, null);
    }

    @Override
    public void clearUserInformation(Activity activity)
    {
        AnalyticsManager.getInstance(activity).setUserInformation(AnalyticsManager.ValueType.EMPTY, AnalyticsManager.ValueType.EMPTY);
    }

    @Override
    public void onEventCopyReferralCode(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.INVITE_FRIEND//
            , AnalyticsManager.Action.REFERRAL_CODE_COPIED, AnalyticsManager.Label.PROFILE_EDITED, null);
    }

    @Override
    public void setExceedBonus(Activity activity, boolean isExceedBonus)
    {
        AnalyticsManager.getInstance(activity).setExceedBonus(isExceedBonus);
    }
}
