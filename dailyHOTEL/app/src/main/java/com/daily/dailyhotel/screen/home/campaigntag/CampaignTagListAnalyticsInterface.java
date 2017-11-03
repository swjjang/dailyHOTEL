package com.daily.dailyhotel.screen.home.campaigntag;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.entity.CampaignTag;

/**
 * Created by android_sam on 2017. 8. 16..
 */

public interface CampaignTagListAnalyticsInterface extends BaseAnalyticsInterface
{
    void onCampaignTagEvent(Activity activity, CampaignTag campaignTag, int listCount);

    void onEventStayClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR, boolean provideRewardSticker);

    void onEventGourmetClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR);

    void onEventStayWishClick(Activity activity, boolean wish);

    void onEventGourmetWishClick(Activity activity, boolean wish);
}
