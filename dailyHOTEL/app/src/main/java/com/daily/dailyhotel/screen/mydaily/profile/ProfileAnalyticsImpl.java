package com.daily.dailyhotel.screen.mydaily.profile;

import android.app.Activity;
import android.content.Context;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class ProfileAnalyticsImpl implements ProfilePresenter.ProfileAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.PROFILE, null);
    }

    @Override
    public void onScreenLogOut(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE, null);
    }

    @Override
    public void onClearUserInformation(Context context)
    {
        AnalyticsManager.getInstance(context).setUserInformation(AnalyticsManager.ValueType.EMPTY, AnalyticsManager.ValueType.EMPTY);
    }

    @Override
    public void onEventCopyReferralCode(Context context)
    {
        AnalyticsManager.getInstance(context).recordEvent(AnalyticsManager.Category.INVITE_FRIEND//
            , AnalyticsManager.Action.REFERRAL_CODE_COPIED, AnalyticsManager.Label.PROFILE_EDITED, null);
    }

    @Override
    public void onExceedBonus(Context context, boolean isExceedBonus)
    {
        AnalyticsManager.getInstance(context).setExceedBonus(isExceedBonus);
    }
}
