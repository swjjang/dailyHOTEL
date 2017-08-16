package com.daily.dailyhotel.screen.home.campaigntag;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;

/**
 * Created by iseung-won on 2017. 8. 16..
 */

public interface CampaignTagListAnalyticsInterface extends BaseAnalyticsInterface
{
    void onCampaignTagEvent(Activity activity, int tagIndex, int listCount);
}
