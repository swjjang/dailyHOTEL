package com.daily.dailyhotel.screen.home.campaigntag;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.CampaignTag;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iseung-won on 2017. 8. 15..
 */

public class CampaignTagListAnalyticsImpl implements CampaignTagListAnalyticsInterface
{
    @Override
    public void onCampaignTagEvent(Activity activity, CampaignTag campaignTag, int listCount)
    {
        if (activity == null || campaignTag == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        int index = campaignTag.index;
        String tagIndex = Integer.toString(index);

        String category = campaignTag.serviceType;
        if (DailyTextUtils.isTextEmpty(category) == true)
        {
            // do nothing
        } else if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(category.toUpperCase()))
        {
            category = AnalyticsManager.ValueType.STAY;
        } else if (Constants.ServiceType.GOURMET.name().equalsIgnoreCase(category.toUpperCase()))
        {
            category = AnalyticsManager.ValueType.GOURMET;
        }

        params.put(AnalyticsManager.KeyType.CATEGORY, category);
        params.put(AnalyticsManager.KeyType.TAG, tagIndex);
        params.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(listCount));

        if (listCount == 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
                , AnalyticsManager.Action.TAG_SEARCH_NOT_FOUND, tagIndex, params);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH //
                , AnalyticsManager.Action.TAG_SEARCH, tagIndex, params);
        }
    }
}
