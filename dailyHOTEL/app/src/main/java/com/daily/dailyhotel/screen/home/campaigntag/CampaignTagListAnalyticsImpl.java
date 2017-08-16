package com.daily.dailyhotel.screen.home.campaigntag;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by iseung-won on 2017. 8. 15..
 */

public class CampaignTagListAnalyticsImpl implements CampaignTagListAnalyticsInterface
{
    @Override
    public void onCampaignTagEvent(Activity activity, int tagIndex, int listCount)
    {
        if (activity == null)
        {
            return;
        }

        if (listCount == 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
                , AnalyticsManager.Action.TAG_SEARCH_NOT_FOUND, Integer.toString(tagIndex), null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
                , AnalyticsManager.Action.TAG_SEARCH, Integer.toString(tagIndex), null);
        }
    }
}
