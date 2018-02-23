package com.daily.dailyhotel.screen.mydaily.profile;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class ProfileAnalyticsImpl implements ProfilePresenter.ProfileAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.PROFILE, null);
    }

    @Override
    public void onScreenLogout(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE, null);
    }

    @Override
    public void clearUserInformation(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).setUserInformation(AnalyticsManager.ValueType.EMPTY, AnalyticsManager.ValueType.EMPTY);
    }

    @Override
    public void setExceedBonus(Activity activity, boolean isExceedBonus)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).setExceedBonus(isExceedBonus);
    }

    @Override
    public void onEventPrivacyValidMonth(Activity activity, int month)
    {
        if (activity == null)
        {
            return;
        }

        if (month < 12)
        {
            month = 12;
        }

        int year = month / 12;
        String label = year > 1 ? year + "yrs" : year + "yr";

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REGISTRATION //
            , AnalyticsManager.Action.PRIVACY, label, null);
    }

    @Override
    public void onEventMemberLeaveClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.MEMBER_LEAVE //
            , "member_leave_init", null, null);
    }
}
