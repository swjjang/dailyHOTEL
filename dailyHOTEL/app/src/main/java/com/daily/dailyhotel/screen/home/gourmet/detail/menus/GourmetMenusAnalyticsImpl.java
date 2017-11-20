package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetMenusAnalyticsImpl implements GourmetMenusPresenter.GourmetMenusAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.GOURMET_MENU_DETAIL, null);
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_MENU_BACK_CLICK, "menu_detail", null);
    }

    @Override
    public void onEventFlicking(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_MENU_FLICKING, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventImageClick(Activity activity, String label)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.GOURMET_MENU_IMG_CLICK, label, null);
    }

    @Override
    public void onEventOpenCartMenuClick(Activity activity, int gourmetIndex, int menuCount)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.BOOKINGCLICKED_ + "1", (menuCount > 1 ? AnalyticsManager.Label.MULTI : AnalyticsManager.Label.SINGLE) + "_" + Integer.toString(gourmetIndex), null);
    }
}
