package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.app.Activity;

import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetFilterAnalyticsImpl implements GourmetFilterInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_CURATION, null);
    }

    @Override
    public void onConfirmClick(Activity activity, GourmetSuggest suggest, GourmetFilter filter, int listCountByFilter)
    {
        if (activity == null)
        {
            return;
        }

    }

    @Override
    public void onBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
    }

    @Override
    public void onResetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }

    @Override
    public void onEmptyResult(Activity activity, GourmetFilter filter)
    {
        if (activity == null)
        {
            return;
        }

    }
}
